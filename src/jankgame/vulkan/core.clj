(ns jankgame.vulkan.core
  (:import [org.lwjgl.opengl GL GL11]
           [org.lwjgl.glfw GLFW]
           [org.lwjgl.system MemoryStack]
           [java.nio IntBuffer]))

(def instance (atom nil))
(def physical-device (atom nil))
(def device (atom nil))
(def queue (atom nil))
(def command-pool (atom nil))
(def surface (atom nil))

(defn create-window [width height title]
  "Create a GLFW window for rendering"
  (if (not (GLFW/glfwInit))
    (throw (Exception. "Failed to initialize GLFW")))

  (GLFW/glfwWindowHint GLFW/GLFW_CONTEXT_VERSION_MAJOR 3)
  (GLFW/glfwWindowHint GLFW/GLFW_CONTEXT_VERSION_MAJOR 3)
  (GLFW/glfwWindowHint GLFW/GLFW_RESIZABLE GLFW/GLFW_FALSE)
  (GLFW/glfwWindowHint GLFW/GLFW_VISIBLE GLFW/GLFW_TRUE)

  (let [window (GLFW/glfwCreateWindow width height title 0 0)]
    (if (= window 0)
      (throw (Exception. "Failed to create GLFW window")))

    ; Make context current and show window
    (GLFW/glfwMakeContextCurrent window)
    (GLFW/glfwShowWindow window)
    window))

(defn destroy-window [window]
  "Clean up GLFW window"
  (GLFW/glfwDestroyWindow window)
  (GLFW/glfwTerminate))

(defn poll-events []
  "Poll window events"
  (GLFW/glfwPollEvents))

(defn window-should-close? [window]
  "Check if window should close"
  (= (GLFW/glfwWindowShouldClose window) GLFW/GLFW_TRUE))

(defn initialize-opengl []
  "Initialize OpenGL"
  (try
    (GL/createCapabilities)
    (GL11/glViewport 0 0 1280 720)
    (GL11/glClearColor 0.1 0.1 0.15 1.0)
    (GL11/glEnable GL11/GL_DEPTH_TEST)
    (println "OpenGL initialized")
    (catch Exception e
      (println (format "OpenGL error: %s" (.getMessage e))))))

(defn initialize-vulkan [window]
  "Initialize rendering for the given window"
  (initialize-opengl))

(defn render-scene [rotation]
  "Render the scene with the ball"
  (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT GL11/GL_DEPTH_BUFFER_BIT))

  ; Set up a simple perspective
  (GL11/glMatrixMode GL11/GL_PROJECTION)
  (GL11/glLoadIdentity)
  (let [aspect (/ 1280.0 720.0)]
    (GL11/glFrustum -0.5 0.5 (/ -0.5 aspect) (/ 0.5 aspect) 1.0 100.0))

  (GL11/glMatrixMode GL11/GL_MODELVIEW)
  (GL11/glLoadIdentity)
  (GL11/glTranslatef 0.0 0.0 -5.0)
  (GL11/glRotatef (* (float rotation) 57.2958) 0.0 1.0 0.0)

  ; Enable lighting for 3D effect
  (GL11/glEnable GL11/GL_LIGHTING)
  (GL11/glEnable GL11/GL_LIGHT0)

  ; Set up a light source
  (let [light-pos (float-array [2.0 3.0 2.0 1.0])
        light-ambient (float-array [0.2 0.2 0.2 1.0])
        light-diffuse (float-array [1.0 0.9 0.8 1.0])
        light-specular (float-array [1.0 1.0 1.0 1.0])]
    (GL11/glLight GL11/GL_LIGHT0 GL11/GL_POSITION light-pos)
    (GL11/glLight GL11/GL_LIGHT0 GL11/GL_AMBIENT light-ambient)
    (GL11/glLight GL11/GL_LIGHT0 GL11/GL_DIFFUSE light-diffuse)
    (GL11/glLight GL11/GL_LIGHT0 GL11/GL_SPECULAR light-specular))

  ; Material properties for the sphere
  (let [mat-ambient (float-array [0.8 0.3 0.3 1.0])
        mat-diffuse (float-array [0.8 0.3 0.3 1.0])
        mat-specular (float-array [1.0 1.0 1.0 1.0])
        mat-shininess (float-array [50.0])]
    (GL11/glMaterial GL11/GL_FRONT GL11/GL_AMBIENT mat-ambient)
    (GL11/glMaterial GL11/GL_FRONT GL11/GL_DIFFUSE mat-diffuse)
    (GL11/glMaterial GL11/GL_FRONT GL11/GL_SPECULAR mat-specular)
    (GL11/glMaterial GL11/GL_FRONT GL11/GL_SHININESS mat-shininess))

  ; Draw a proper 3D sphere using quadric strips
  (let [radius 1.0
        stacks 32
        slices 32]
    (doseq [i (range stacks)]
      (let [lat0 (/ (* Math/PI (- (* i 0.5) (/ stacks 2.0))) stacks)
            z0 (* radius (Math/sin lat0))
            r0 (* radius (Math/cos lat0))
            lat1 (/ (* Math/PI (- (* (+ i 1) 0.5) (/ stacks 2.0))) stacks)
            z1 (* radius (Math/sin lat1))
            r1 (* radius (Math/cos lat1))]
        (GL11/glBegin GL11/GL_QUAD_STRIP)
        (doseq [j (range (inc slices))]
          (let [lng (/ (* 2 Math/PI j) slices)
                x (* (Math/cos lng))
                y (* (Math/sin lng))]
            (GL11/glNormal3f (* x r0) (* y r0) z0)
            (GL11/glVertex3f (* x r0) (* y r0) z0)
            (GL11/glNormal3f (* x r1) (* y r1) z1)
            (GL11/glVertex3f (* x r1) (* y r1) z1)))
        (GL11/glEnd))))

  (GL11/glDisable GL11/GL_LIGHTING))

(defn swap-buffers [window]
  "Swap the window buffers"
  (GLFW/glfwSwapBuffers window))

(defn cleanup-vulkan []
  "Clean up rendering resources"
  (println "OpenGL cleaned up"))
