-- Get information about the android.
local androidInfo = android.getSelf()

-- Store the position information as a "home" position for later use.
local homePosition = {x = androidInfo.posX, y = androidInfo.posY, z = androidInfo.posZ}

-- This is a table or list of names of mob types to search for and attack.
local targetTypes = {"zombie", "skeleton", "spider"}

local function guard()
    print("Guarding location. \n Press ENTER to stop guarding.")

    while true do
        -- If the android is attacking
        -- then skip for now.
        if android.currentTask() == "attackingMob" then
            goto pass
        end

        -- Iterate over the table of target types
        -- to search for a mob that matches a type.
        for _,type in ipairs(targetTypes) do
            local mob = android.getClosestMob(type)

            -- If the uuid is not nil then we have found a mob
            if mob["uuid"] ~= nil then
                -- Tell the android to attack the mob and skip to the next loop.
                android.attack(mob["uuid"])

                goto pass
            end
        end

        -- Head home if no mob was found.
        android.moveTo(homePosition)

        ::pass::

        -- Sleep to allow reading terminal input.
        sleep()
    end
end

-- This function listens for the "Enter" key being pressed in the terminal
-- if the key is pressed then break from the while loop so that the program quits.
local function listenForCancel()
    while true do
        -- Listen for the "key" event, which is
        -- fired whenever a player pressess a key
        -- in the terminal.
        local _, key = os.pullEvent("key")

        -- If the key pressed matches the enter key then we
        -- know that the user wants to stop the android from
        -- following them and so we break out of the loop
        -- to end the program early.
        if key == keys.enter then
            break
        end
    end

    print("Cancel key pressed, stopping android from guarding.")
end

-- Androids cannot move without fuel so we check if
-- the android has fuel and if it does not, we assume
-- whatever the android is currently holding is fuel
-- and try to use that to refuel the android.
local function checkFuel()
    -- Check the fuel level, if it is less than 1 then we know
    -- the android has no fuel left in reserve.
    if android.fuelLevel() < 1 then
        -- Try to refuel.
        local failed = android.refuel()

        -- If failed is true then the android failed to refuel.
        return not failed
    end

    return true
end

if not checkFuel() then
    -- Print a message telling the user that the android has no fuel
    -- and cannot refuel with what they are holding.
    print("The android needs fuel in order to move, place some fuel in its main hand.")
    return
end

parallel.waitForAny(guard, listenForCancel)

-- The android might still be moving
-- when the program is about to quit
-- this tells the android to stop whatever
-- they are doing immediately.
android.cancelTask()