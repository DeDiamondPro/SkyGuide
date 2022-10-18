# SkyGuide

SkyGuide is a mod that aims to improve the skyblock experience in a vanilla-like way.

## Current Features

Map of skyblock, including portals and npcs *(not available on all islands since some renders aren't done yet)*

![Image of map](https://i.dediamondpro.dev/HewzvqHG.png)

Customizable Mini-Map *(not available on all islands since some renders aren't done yet)*

![Image of mini-map](https://i.dediamondpro.dev/3oUBsq8F.png)

Intelligent waypoint system that can guide you to portals in order to get to your destination or warp to the nearest
warp.

## FAQ

<details>
<summary>Where can I get support?</summary>
<br>
<strong>You can get support in our <a href="https://discord.gg/XtAuqsJWby">Discord</a>.</strong>
</details>

<details>
<summary>Is SkyGuide bannable?</summary>
<br>
<strong>No</strong>, I do not believe it is bannable but as any mod it is use at your own risk.
</details>
<details>
<summary>Is the Mini-Map against Hypixel's rules?</summary>
<br>
Due to Hypixel's vague rules regarding client modifications it is hard to say for certain and this mod stays use at your
own risk but <strong>I do not believe the mini-map is against the rules</strong>, here is my reasoning:

The mini-maps Hypixel is saying are not allowed take their data straight from the world and display it on your HUD,
while SkyGuide's mini-map uses pre-rendered images. This is important because Hypixel states the following in their rules:
> a general rule of thumb is that any modification which provides any significant advantage to the players in any of our games - even if not in the specific game you are playing - using them anywhere on our server will be against our rules.

So a normal mini-map doesn't provide an unfair advantage in skyblock, but it does in some pvp games, especially if it 
has an entity radar. SkyGuide's mini-map uses pre rendered images and hence doesn't work in any games other than 
skyblock. So SkyGuide's mini-map cannot provide an unfair advantage anywhere on the Hypixel network.
</details>

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

#### For multilayered images

- Reduce Y-max clip until you have the layer you want
- In the json set the value before the image to the Y-max clip
