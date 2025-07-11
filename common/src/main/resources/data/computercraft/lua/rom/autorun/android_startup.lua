if android == nil then
    return
end

-- Setup paths
local sPath = shell.path()

if android then
   sPath = sPath .. ":/rom/programs/android"
end

shell.setPath(sPath)

-- Setup motd

local mPath = settings.get("motd.path")

mPath = mPath .. ":/rom/.android_motd.txt"

settings.set("motd.path", mPath)