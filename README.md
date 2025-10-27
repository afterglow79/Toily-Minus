This is a mod manager built to run on Linux (albeit with a bad UI). It does not locally download mods like CurseForge, but assumes you keep all your mods in a folder somewhere on your computer (which you input) and allows for the creation of custom-named modpacks with the given files. Currently does not differentiate between any of the loaders (Forge, Fabric, etc.) but I hope to implement that.

----
# Features
- Custom modpack creation
- Ability to save & load immediately, or save for later
- Loading of already saved modpacks
  - Stores your modpack in a folder called "modpacks/[modpack_name], this keeps all the .jar files in one place (at the cost of storage)
  - A lot of loading is handled via txt files, so it should be largely storage-efficient (besides above), and it should be fairly fast at doing so.
  
# What I plan to implement
- Differentiation between loaders (reasonably low priority)
- Ability to edit modpacks (high priority)
- Make the UI look better (this damn well may never happen)
- Optimize it (medium priority, it should run fine enough)
- Anything else that I can think of that would be useful
