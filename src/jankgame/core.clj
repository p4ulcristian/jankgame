(ns jankgame.core
  (:require [jankgame.vulkan.core :as vulkan]
            [jankgame.graphics.renderer :as renderer]
            [jankgame.graphics.lighting :as lighting]
            [jankgame.graphics.ball :as ball]
            [jankgame.math.matrix :as matrix])
  (:gen-class))

(def config
  {:width 1280
   :height 720
   :title "Jankgame - Vulkan Ball with Lighting"
   :fps 60})

(defn run-loop [window lights camera max-frames]
  "Main rendering loop"
  (loop [frame 0]
    (if (or (vulkan/window-should-close? window) (>= frame max-frames))
      (println "Closing window...")
      (do
        (vulkan/poll-events)
        (renderer/update-frame)
        (let [rotation (renderer/render-frame lights camera)]
          ; Actually render to the screen
          (vulkan/render-scene rotation)
          (vulkan/swap-buffers window))

        ; Print status every 60 frames
        (when (= (mod frame 60) 0)
          (println (format "Frame: %d"
                          frame)))

        (recur (inc frame))))))

(defn setup-scene []
  "Initialize the scene with objects and lights"
  ; Create renderer
  (renderer/create-renderer)

  ; Add a ball with lighting
  (renderer/add-lit-ball
    {:position [0.0 0.0 -5.0]
     :color [0.8 0.3 0.3]  ; Red ball
     :light-color [1.0 1.0 1.0]
     :radius 1.0})

  ; Add lights
  (let [light1 (lighting/create-light
                 {:position [2.0 3.0 2.0]
                  :intensity 1.0
                  :color [1.0 0.9 0.8]
                  :radius 100.0})
        light2 (lighting/create-light
                 {:position [-2.0 2.0 2.0]
                  :intensity 0.6
                  :color [0.8 0.9 1.0]
                  :radius 100.0})]
    [light1 light2]))

(defn setup-camera []
  "Setup camera for the scene"
  {:position [0.0 0.0 0.0]
   :target [0.0 0.0 -5.0]
   :up [0.0 1.0 0.0]
   :fov 45.0})

(defn -main
  "Main entry point"
  [& args]
  (try
    (println "Initializing Jankgame...")
    (println (format "Creating %dx%d window: %s"
                    (:width config)
                    (:height config)
                    (:title config)))

    ; Create window
    (let [window (vulkan/create-window (:width config) (:height config) (:title config))]
      (try
        ; Initialize Vulkan
        (vulkan/initialize-vulkan window)

        ; Setup scene
        (let [lights (setup-scene)
              camera (setup-camera)]
          (println "Scene setup complete. Starting render loop...")
          (println "Press Ctrl+C or close the window to exit...")

          ; Run indefinitely until window closes
          (run-loop window lights camera Integer/MAX_VALUE))

        (finally
          ; Cleanup
          (println "Cleaning up...")
          (vulkan/cleanup-vulkan)
          (renderer/cleanup-renderer)
          (vulkan/destroy-window window))))

    (println "Goodbye!")
    (System/exit 0)

    (catch Exception e
      (println (format "Error: %s" (.getMessage e)))
      (.printStackTrace e)
      (System/exit 1))))
