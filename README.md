<div align="center">
  
# `SkyGuide`
A mod that aims to improve the Hypixel SkyBlock experience in a vanilla-like way.
  
</div>

## Current features
Map of SkyBlock, including portals and NPCs. *(not available on all islands since some renders aren't done yet)*

![Image of map](https://i.ascella.host/HewzvqHG.png)

Customizable Mini-Map. *(not available on all islands since some renders aren't done yet)*

![Image of mini-map](https://i.ascella.host/3oUBsq8F.png)

Intelligent waypoint system that can guide you to portals in order to get to your destination or warp to the nearest
warp.

## FaQ

<details>
    <summary>Where can I get support?</summary>
  
**Support is available in our [Discord server](https://discord.gg/XtAuqsJWby).**

</details>

<details>
    <summary>Is SkyGuide bannable on Hypixel?</summary>

**No**, it should not be bannable in its current state. __Though with all mods, it is and always will be "use at your own risk"!__

</details>

<details>
    <summary>Is the Mini-Map against Hypixel's rules/bannable?</summary>

Due to Hypixel's vague rules regarding client modifications it is hard to say for certain and this mod stays use at your
own risk but **I do not believe the mini-map is against the rules**, here is my reasoning:

The mini-maps Hypixel is saying are not allowed take their data straight from the world and display it on your HUD,
while SkyGuide's mini-map uses pre-rendered images. This is important because Hypixel states the following in their rules:
> a general rule of thumb is that any modification which provides any significant advantage to the players in any of our games - even if not in the specific game you are playing - using them anywhere on our server will be against our rules.

So a normal mini-map doesn't provide an unfair advantage in SkyBlock, but it does in some pvp games, especially if it 
has an entity radar. SkyGuide's mini-map uses pre rendered images and hence doesn't work in any games other than 
SkyBlock. So SkyGuide's mini-map cannot provide an unfair advantage anywhere on the Hypixel network.

</details>

## License
SkyGuide is licensed under LGPL unless explicitly noted.

All of SkyGuide's images in the `data` subdirectory are licensed under CC-BY-NC-SA-4.0

## Dev stuff

#### How to render images

Images are rendered using chunky

In chunky make a scene of all chunks of an island and position the camera accordingly,
set the camera type to parallel, then in the map tab get the top left and bottom right corner and calculate the size,
this size should be inputted in the scene tab depending on the texture quality.

- Low = 1 pixel per block
- Medium = 2 pixels per block
- High = 4 pixels per block

Now enable transparent sky and set the SPP to 250 (this is plenty for our purpose and speeds up rendering)

#### For multi-layered images

- Reduce Y-max clip until you have the layer you want
- In the json set the value before the image to the Y-max clip
