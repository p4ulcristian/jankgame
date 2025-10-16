(ns jankgame.math.matrix)

(defn vec3 [x y z]
  "Create a 3D vector"
  [x y z])

(defn vec-add [v1 v2]
  "Add two vectors"
  (mapv + v1 v2))

(defn vec-sub [v1 v2]
  "Subtract two vectors"
  (mapv - v1 v2))

(defn vec-mul [v scalar]
  "Multiply vector by scalar"
  (mapv (partial * scalar) v))

(defn vec-dot [v1 v2]
  "Dot product of two vectors"
  (reduce + (map * v1 v2)))

(defn vec-cross [v1 v2]
  "Cross product of two vectors"
  [(- (* (v1 1) (v2 2)) (* (v1 2) (v2 1)))
   (- (* (v1 2) (v2 0)) (* (v1 0) (v2 2)))
   (- (* (v1 0) (v2 1)) (* (v1 1) (v2 0)))])

(defn vec-length [v]
  "Length of vector"
  (Math/sqrt (reduce + (map * v v))))

(defn vec-normalize [v]
  "Normalize vector to unit length"
  (let [len (vec-length v)]
    (if (> len 0) (mapv (partial / len) v) v)))

(defn matrix-mul [m1 m2]
  "Multiply two 4x4 matrices"
  (into []
    (for [i (range 4)]
      (into []
        (for [j (range 4)]
          (reduce + (for [k (range 4)]
                      (* (get-in m1 [i k]) (get-in m2 [k j])))))))))

(defn identity-matrix []
  "Create 4x4 identity matrix"
  [[1 0 0 0]
   [0 1 0 0]
   [0 0 1 0]
   [0 0 0 1]])

(defn translate [matrix tx ty tz]
  "Translate matrix"
  (let [t-mat [[1 0 0 tx]
               [0 1 0 ty]
               [0 0 1 tz]
               [0 0 0 1]]]
    (matrix-mul matrix t-mat)))

(defn rotate-y [matrix angle]
  "Rotate matrix around Y axis"
  (let [cos (Math/cos angle)
        sin (Math/sin angle)
        r-mat [[cos 0 sin 0]
               [0 1 0 0]
               [(- sin) 0 cos 0]
               [0 0 0 1]]]
    (matrix-mul matrix r-mat)))
