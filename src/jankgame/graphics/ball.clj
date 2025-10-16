(ns jankgame.graphics.ball
  (:import [java.nio FloatBuffer]))

(defn generate-uv-sphere
  "Generate UV sphere geometry with given stacks and slices"
  [radius stacks slices]
  (let [vertices (transient [])
        indices (transient [])
        vertex-count (atom 0)]

    ; Generate vertices
    (doseq [i (range (inc stacks))]
      (let [stack-angle (/ (* Math/PI i) stacks)]
        (doseq [j (range (inc slices))]
          (let [slice-angle (/ (* 2 Math/PI j) slices)
                x (* radius (Math/sin stack-angle) (Math/cos slice-angle))
                y (* radius (Math/cos stack-angle))
                z (* radius (Math/sin stack-angle) (Math/sin slice-angle))
                nx (/ x radius)
                ny (/ y radius)
                nz (/ z radius)]
            (conj! vertices [x y z nx ny nz])))))

    ; Generate indices for triangle strips
    (doseq [i (range stacks)]
      (doseq [j (range slices)]
        (let [first (* (+ i 1) (inc slices))
              second (* i (inc slices))]
          (conj! indices first)
          (conj! indices (+ second j))
          (conj! indices (+ first j))
          (conj! indices (+ first j))
          (conj! indices (+ second j))
          (conj! indices (+ second (inc j))))))

    (let [verts (persistent! vertices)
          inds (persistent! indices)]
      {:vertices verts
       :indices inds
       :vertex-count (count verts)
       :index-count (count inds)})))

(defn create-ball
  "Create a ball mesh with specified parameters"
  [{:keys [radius stacks slices color]
    :or {radius 1.0 stacks 32 slices 32 color [1.0 1.0 1.0]}}]
  (let [geometry (generate-uv-sphere radius stacks slices)]
    (assoc geometry :color color)))

(defn ball-with-lighting
  "Create a ball mesh optimized for lighting"
  [{:keys [radius color light-color]
    :or {radius 1.0 color [0.8 0.3 0.3] light-color [1.0 1.0 1.0]}}]
  (create-ball {:radius radius :stacks 64 :slices 64 :color color}))
