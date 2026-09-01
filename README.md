# BannerMaker EMC Folia Edition
>Feel banner is fun, but you don't know how to craft?  
>It's too hard to craft, so you make wrong usually?  
>You must try this plugin.
>Jyhsu200's description of BannerMaker

This is a reworked version of jyhsu2000's bannermaker that had two goals in mind at its creation.
The first was to achieve Folia support by replacing the ChestGUI framework with InventoryGUI, a folia supported framework.
The second was to make the gameplay related changes needed to make the plugin a better fit for the EarthMC minecraft server.

Since then the difference between the original plugin has expanded some as Jyhsu200 added new features and updated plugin.

## Description

Bannermaker is centered around being able to open a GUI for designing and creation of banners and the sharing of it.
This version demands of you to have the items needed to craft the banner as you would in vanilla and consumes the materials that are consumable, while letting you keep the non-consumable.
Banners can be up to the highest possible of patterns per banner, higher than vanilla.

## Features

- Design and save banners
- Look up recipe of banners
- Multi-language support (Setting in `config.yml`) (FUTURE)
- Material estimates
- Craft banner by using materials
- Show/share your banners to others

## How to use

1. Run command `/bm` to open GUI
2. Enjoy it

## Commands

(Permissions are up to be changed)

| **Command**    | **Description**                                  | **Permission**       |
|----------------|--------------------------------------------------|----------------------|
| `/bm`          | Open GUI                                         | `bannermaker.use`    |
| `/bm help`     | Command list                                     |                      |
| `/bm hand`     | View banner info of the banner in hand           | `bannermaker.hand`   |
| `/bm see`      | View banner info of the banner you're looking at | `bannermaker.see`    |
| `/bm view ...` | View banner info of the banner command           | `bannermaker.view`   |
| `/bm reload`   | Reload config                                    | `bannermaker.reload` |

## Major Permission Sets

| **Permission**       | **Description**                | **Default** |
|----------------------|--------------------------------|-------------|
| `bannermaker.player` | Permissions for normal players | True        |
| `bannermaker.admin`  | Whole permission               | OP          |
| `bannermaker.show`   | Show banner info to players    | OP          |

## Other Permissions

| **Permission**                        | **Description**                                                 | **Default** |
|---------------------------------------|-----------------------------------------------------------------|-------------|
| `bannermaker.getbanner`               | Get banners from GUI                                            | OP          |
| `bannermaker.getbanner.complex-craft` | Bypass 6-patterns limit when getting banner (Enabled in config) | OP          |
| `bannermaker.getbanner.free`          | Get banners for free                                            | OP          |

*All detailed permissions can be found in [plugin.yml](src/main/resources/plugin.yml)*

## Pictures

Images property of Jyhsu200
![Main menu](http://i.imgur.com/rMTTfsE.png)  
![Create banner](http://i.imgur.com/HB6Dhm3.png)  
![Banner info](http://i.imgur.com/Xydmcbj.png)  
![Alphabet & Number](http://i.imgur.com/tGHmakp.png)
