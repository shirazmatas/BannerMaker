# BannerMaker
Feel banner is fun, but you don't know how to craft?  
It's too hard to craft, so you make wrong usually?  
You must try this plugin.
Reworked version of jyhsu2000's bannermaker.
Folia supported as ChestGUI swapped with InventoryGUI.
## Description

Using just one command, you can use GUI of this plugin to design any kind of banner.  
You don't need to know how to craft. All you need to know is how it will look like.

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

![Main menu](http://i.imgur.com/rMTTfsE.png)  
![Create banner](http://i.imgur.com/HB6Dhm3.png)  
![Banner info](http://i.imgur.com/Xydmcbj.png)  
![Alphabet & Number](http://i.imgur.com/tGHmakp.png)

[![bStats](https://bstats.org/signatures/bukkit/BannerMaker.svg)](https://bstats.org/plugin/bukkit/BannerMaker)
