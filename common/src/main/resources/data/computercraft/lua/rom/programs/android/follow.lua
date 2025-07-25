-- The android will attempt to move to the players position.
local function followPlayer()
    local player = android.getClosestPlayer()

    -- UUIDs are unique identifiers that are used to
    -- reference and keep track of entities in the game.
    local playerUUID = player["uuid"]

    print("Following closest player. \n Press ENTER to stop following.")

    while true do
        -- We can find out what the android
        -- is currently doing by calling android.currentTask()
        -- which will return a string to describe the action.
        local currentTask = android.currentTask()

        -- If the android is idle (not moving) then
        -- tell it to start moving to the player.
        if currentTask == "idle" then
            android.goTo(playerUUID)
        end

        -- Yield so that we can listen for terminal input.
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

    print("Cancel key pressed, stopping android from following player.")
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

parallel.waitForAny(followPlayer, listenForCancel)

-- The android might still be moving
-- when the program is about to quit
-- this tells the android to stop whatever
-- they are doing immediately.
android.cancelTask()