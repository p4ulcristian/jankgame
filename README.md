# Jankgame - Vulkan Graphics Engine

A Clojure-based graphics engine using Vulkan for rendering, featuring an example with a lit sphere.

## Features

- **Vulkan Rendering**: Modern graphics API integration via LWJGL
- **Lighting System**: Phong and Lambert diffuse lighting models
- **Ball Geometry**: UV-sphere mesh generation with customizable tesselation
- **Clojure**: Functional programming approach to graphics
- **GLFW Window Management**: Cross-platform window and input handling

## Project Structure

```
src/
├── jankgame/
│   ├── core.clj              # Main application entry point
│   ├── vulkan/
│   │   └── core.clj          # Vulkan initialization and window management
│   ├── graphics/
│   │   ├── renderer.clj      # Rendering engine and frame updates
│   │   ├── ball.clj          # Sphere mesh generation
│   │   └── lighting.clj      # Lighting calculations (Phong, Lambert)
│   └── math/
│       └── matrix.clj        # Vector and matrix math utilities
```

## Building

Requires:
- Java 11+
- Clojure Tools (clj)
- Vulkan SDK
- GLFW development libraries

### Install Dependencies

On Linux:
```bash
# Ubuntu/Debian
sudo apt-get install libvulkan-dev libglfw3-dev

# Arch
sudo pacman -S vulkan-headers glfw-x11
```

Install Clojure tools from https://clojure.org/guides/install_clojure

### Running the Example

```bash
clj -M:run
```

Or download dependencies first:
```bash
clj -P          # Prepare (download all dependencies)
clj -M:run      # Run the example
```

This will:
1. Create a 1280x720 window
2. Initialize Vulkan
3. Create a red sphere with lighting
4. Add two light sources
5. Run the rendering loop for 300 frames

## Example Output

```
Initializing Jankgame...
Creating 1280x720 window: Jankgame - Vulkan Ball with Lighting
Vulkan initialized
Scene setup complete. Starting render loop...
Running for 300 frames...
Frame: 0 FPS: 60.0
Frame: 60 FPS: 60.0
...
Closing window...
Cleaning up...
Goodbye!
```

## Architecture

### Rendering Pipeline

1. **Initialization**: Vulkan instance and device creation via GLFW
2. **Scene Setup**: Objects (balls) and lights are added to the renderer
3. **Frame Loop**: Each frame:
   - Update object rotations
   - Calculate lighting for each surface normal
   - Render the frame
4. **Cleanup**: Release Vulkan and GLFW resources

### Lighting Model

The engine supports two lighting models:

- **Phong Lighting**: Includes ambient, diffuse, and specular components
  ```
  I = Ia * ka + Id * kd * (N · L) + Is * ks * (R · V)^n
  ```

- **Lambert Diffuse**: Simple diffuse lighting with ambient fallback
  ```
  I = Ia + Id * kd * max(0, N · L)
  ```

### Geometry

The sphere is generated using UV-sphere parameterization:
```
x = r * sin(θ) * cos(φ)
y = r * cos(θ)
z = r * sin(θ) * sin(φ)
```

## Extending

### Adding More Objects

Modify the `setup-scene` function in `core.clj`:

```clojure
(renderer/add-lit-ball
  {:position [0.0 0.0 -5.0]
   :color [1.0 0.0 0.0]  ; Red
   :radius 2.0})
```

### Changing Lighting

Adjust light properties in `setup-scene`:

```clojure
(lighting/create-light
  {:position [2.0 3.0 2.0]
   :intensity 1.5
   :color [1.0 1.0 1.0]
   :radius 100.0})
```

### Custom Mesh

Create new mesh generators in `graphics/` and register them in the renderer.

## License

MIT

## Notes

This is a work-in-progress educational project. The Vulkan rendering pipeline is simplified for clarity. Production use would require more sophisticated command buffer management, proper synchronization, and error handling.
