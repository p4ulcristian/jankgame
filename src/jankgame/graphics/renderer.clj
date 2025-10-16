(ns jankgame.graphics.renderer
  (:require [jankgame.graphics.ball :as ball]
            [jankgame.graphics.lighting :as lighting]
            [jankgame.math.matrix :as matrix]))

(def renderer-state (atom {:frame 0 :objects []}))

(defn create-renderer []
  "Initialize the renderer"
  (reset! renderer-state {:frame 0 :objects []}))

(defn add-object [obj]
  "Add an object to the renderer"
  (swap! renderer-state update :objects conj obj))

(defn add-lit-ball
  "Add a lit ball to the scene"
  [{:keys [position color light-color radius]
    :or {position [0.0 0.0 0.0] color [0.8 0.3 0.3] light-color [1.0 1.0 1.0] radius 1.0}}]
  (let [ball-mesh (ball/ball-with-lighting {:radius radius :color color})]
    (add-object {:type :ball
                 :mesh ball-mesh
                 :position position
                 :color color
                 :light-color light-color
                 :rotation [0.0 0.0 0.0]
                 :scale [1.0 1.0 1.0]})))

(defn update-frame []
  "Update for the next frame"
  (swap! renderer-state update :frame inc))

(defn render-frame
  "Render a single frame"
  [lights camera]
  (let [state @renderer-state
        frame (:frame state)
        objects (:objects state)
        rotation (atom 0.0)]
    (doseq [obj objects]
      (when (= (:type obj) :ball)
        (let [rotation-speed 0.01
              new-rotation (* rotation-speed frame)]
          (reset! rotation new-rotation)
          (when (and (seq lights) (= (mod frame 60) 0))
            (println (format "Frame %d - Rotation: %.2f rad" frame new-rotation))))))
    @rotation))

(defn cleanup-renderer []
  "Clean up renderer resources"
  (reset! renderer-state nil))
