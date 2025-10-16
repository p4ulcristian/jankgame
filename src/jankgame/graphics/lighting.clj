(ns jankgame.graphics.lighting)

(defn create-light
  "Create a point light source"
  [{:keys [position intensity color radius]
    :or {intensity 1.0 color [1.0 1.0 1.0] radius 100.0}}]
  {:position position
   :intensity intensity
   :color color
   :radius radius
   :type :point})

(defn create-directional-light
  "Create a directional light (like sunlight)"
  [{:keys [direction intensity color]
    :or {intensity 1.0 color [1.0 1.0 1.0]}}]
  {:direction direction
   :intensity intensity
   :color color
   :type :directional})

(defn phong-lighting
  "Calculate Phong lighting for a surface
   Arguments:
   - normal: surface normal vector
   - light-dir: direction from surface to light
   - view-dir: direction from surface to camera
   - diffuse-color: surface diffuse color
   - light-color: light color
   - shininess: material shininess (higher = more specular)"
  [normal light-dir view-dir diffuse-color light-color shininess]
  (let [; Ambient component
        ambient 0.1
        ; Diffuse component
        diffuse (max 0.0 (reduce + (map * normal light-dir)))
        ; Specular component
        reflect-dir (let [dot-prod (reduce + (map * normal light-dir))]
                      (mapv - (mapv (partial * (* 2.0 dot-prod)) normal) light-dir))
        spec-dot (max 0.0 (reduce + (map * reflect-dir view-dir)))
        specular (Math/pow spec-dot shininess)]
    (mapv +
          (mapv (partial * ambient) diffuse-color)
          (mapv (partial * diffuse) (mapv * diffuse-color light-color))
          (mapv (partial * (* 0.5 specular)) light-color))))

(defn lambert-diffuse
  "Simple Lambert diffuse lighting"
  [normal light-dir diffuse-color light-color]
  (let [diffuse (max 0.1 (reduce + (map * normal light-dir)))]
    (mapv (partial * diffuse) (mapv * diffuse-color light-color))))
