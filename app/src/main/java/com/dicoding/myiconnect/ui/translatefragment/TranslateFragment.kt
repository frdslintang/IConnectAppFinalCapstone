package com.dicoding.myiconnect.ui.translatefragment

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dicoding.myiconnect.R
import com.dicoding.myiconnect.databinding.FragmentTranslateBinding
import com.dicoding.myiconnect.ml.IconnectDetect
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService

class TranslateFragment : Fragment() {

    private lateinit var binding: FragmentTranslateBinding
    private lateinit var labels: List<String>
    private var colors = listOf<Int>(
        Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.GRAY, Color.BLACK,
        Color.DKGRAY, Color.MAGENTA, Color.YELLOW, Color.RED
    )
    private val paint = Paint()
    private lateinit var imageProcessor: ImageProcessor
    private lateinit var imageView: ImageView
    private lateinit var cameraDevice: CameraDevice
    private lateinit var handler: Handler
    private lateinit var cameraManager: CameraManager
    private lateinit var textureView: TextureView
    private lateinit var model: IconnectDetect
    private var highestConfidenceIndex: Int = -1
    private val signLanguageTranslator = SignLanguageTranslator()
    private var previousDetection: String? = null
    private var lastDetectionTimestamp: Long = 0
    private lateinit var cameraExecutor: ExecutorService
    private var bitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTranslateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCamera()
    }

    private fun setupCamera() {
        textureView = binding.textureView
        imageView = binding.imageView

        labels = FileUtil.loadLabels(requireContext(), "labelmap.txt")
        imageProcessor = ImageProcessor.Builder().add(ResizeOp(300, 300, ResizeOp.ResizeMethod.BILINEAR)).build()
        model = IconnectDetect.newInstance(requireContext())

        val handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
                openCamera()
            }

            override fun onSurfaceTextureSizeChanged(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
            }

            override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {
                Log.d("MyIconnect", "onSurfaceTextureUpdated")
                // Capture image from camera
                val imageBitmap = textureView.bitmap!!

                // Convert Bitmap to TensorBuffer
                val inputTensorBuffer = convertBitmapToTensorBuffer(imageBitmap)

                // Run model inference
                val outputs = model.process(inputTensorBuffer)

                // Process the outputs as needed
                val outputFeature0 = outputs.outputFeature0AsTensorBuffer
                val outputFeature1 = outputs.outputFeature1AsTensorBuffer
                val outputFeature2 = outputs.outputFeature2AsTensorBuffer
                val outputFeature3 = outputs.outputFeature3AsTensorBuffer

                // Access the model output data
                val scores = outputFeature0.floatArray
                val classes = outputFeature1.floatArray
                val locations = outputFeature2.floatArray

                var mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                val canvas = Canvas(mutable)

                val h = mutable.height
                val w = mutable.width
                paint.textSize = h / 15f
                paint.strokeWidth = h / 85f
                var x = 0

                // Check if scores array is not empty
                if (scores.isNotEmpty()) {
                    scores.forEachIndexed { index, fl ->
                        x = index * 4  // Multiply by 4 to get the correct index in the locations array
                        if (fl > 0.01) {
                            if (highestConfidenceIndex == -1 || fl > scores[highestConfidenceIndex]) {
                                highestConfidenceIndex = index
                            }
                        }
                    }

                    if (highestConfidenceIndex != -1) {
                        // Ensure the index is within bounds
                        if (x + 3 < locations.size) {
                            paint.color = 0xFF58CC02.toInt()
                            paint.style = Paint.Style.STROKE
                            val rectF = RectF(
                                locations[x + 1] * w,
                                locations[x] * h,
                                locations[x + 3] * w,
                                locations[x + 2] * h
                            )

                            val cornerRadius = 80f // Adjust the corner radius as needed
                            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)

                            paint.style = Paint.Style.FILL
                            val confidencePercent = String.format("%.1f%%", scores[highestConfidenceIndex] * 100)
                            canvas.drawText(
                                "${labels[classes[highestConfidenceIndex].toInt()]} $confidencePercent",
                                locations[x + 1] * w,
                                locations[x] * h,
                                paint
                            )

                            // Update the UI caption
                            val currentTime = System.currentTimeMillis()
                            val elapsedTime = currentTime - lastDetectionTimestamp
                            if (elapsedTime > 500) {
                                val detectedObject = labels[classes[highestConfidenceIndex].toInt()]
                                val signLanguageCaption = signLanguageTranslator.translate(detectedObject)
                                // Display the sign language caption on the UI
                                updateCaptionOnUI(signLanguageCaption)

                                // Update the last detection timestamp
                                lastDetectionTimestamp = currentTime

                                // Debug prints
                                Log.d("MyIconnect", "Detected object: ${labels[classes[highestConfidenceIndex].toInt()]}")
                                Log.d("MyIconnect", "Sign language translation: ${signLanguageTranslator.translate(labels[classes[highestConfidenceIndex].toInt()])}")
                            }
                        } else {
                            // Handle the case where the index is out of bounds
                            // Log an error, display a message, or take appropriate action
                        }
                    }
                }

                imageView.setImageBitmap(mutable)

                highestConfidenceIndex = -1
            }

        }

        cameraManager = requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager
        getPermission()
    }

    // Function to convert Bitmap to TensorBuffer
    private fun convertBitmapToTensorBuffer(bitmap: Bitmap): TensorBuffer {
        // Resize the bitmap to the required dimensions
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true)

        // Create an ImageProcessor with resize operation
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(320, 320, ResizeOp.ResizeMethod.BILINEAR))
            .build()

// Create a TensorImage and apply the ImageProcessor
        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(resizedBitmap)
        val processedImage = imageProcessor.process(tensorImage)

// Get the underlying ByteBuffer of the processed image
        val byteBuffer = processedImage.buffer

// Create a TensorBuffer from the ByteBuffer
        val tensorBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 320, 320, 3), DataType.FLOAT32)
        tensorBuffer.loadBuffer(byteBuffer)


        return tensorBuffer
    }

    private fun updateCaptionOnUI(newCaption: String) {
        // Skip updating if the new caption is the same as the previous one
        if (newCaption == previousDetection) {
            return
        }

        val captionTextView = requireView().findViewById<TextView>(R.id.captionTextView)

        // Get the current text of the TextView
        val currentCaption = captionTextView.text.toString()

        // Concatenate the new caption with the current text
        val updatedCaption = "$currentCaption $newCaption"

        // Update the TextView with the updated caption
        captionTextView.text = updatedCaption

        // Update the previous detection for the next check
        previousDetection = newCaption
    }

    override fun onDestroy() {
        super.onDestroy()
        model.close()
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            cameraManager.openCamera(cameraManager.cameraIdList[0], object : CameraDevice.StateCallback() {
                override fun onOpened(camera: CameraDevice) {
                    cameraDevice = camera

                    val surfaceTexture = textureView.surfaceTexture
                    val surface = Surface(surfaceTexture)

                    val captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                    captureRequest.addTarget(surface)

                    cameraDevice.createCaptureSession(listOf(surface), object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(session: CameraCaptureSession) {
                            session.setRepeatingRequest(captureRequest.build(), null, handler)
                        }

                        override fun onConfigureFailed(session: CameraCaptureSession) {
                        }
                    }, handler)
                }

                override fun onDisconnected(camera: CameraDevice) {

                }

                override fun onError(camera: CameraDevice, error: Int) {

                }
            }, handler)
        }
    }

    private fun getPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            getPermission()
        }
    }
}
