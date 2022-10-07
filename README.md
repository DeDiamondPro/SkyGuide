# SkyGuide

## How to render images
Images are rendered using chunky

In chunky make a scene of all chunks of an island and position the camera accordingly,
set the camera type to parallel, then in the map tab get the top left and bottom right corner and calculate the size,
this size should be inputted in the scene tab depending on the texture quality.
- Low = 1 pixel per block
- Medium = 2 pixels per block
- High = 4 pixels per block
Now enable transparent sky and set the SPP to 250 (this is plenty for our purpose and speeds up rendering)

### For multilayered images
- Reduce Y-max clip until you have the layer you want
- In the json set the value before the image to the Y-max clip