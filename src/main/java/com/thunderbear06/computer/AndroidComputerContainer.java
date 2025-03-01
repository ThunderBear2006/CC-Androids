package com.thunderbear06.computer;

import com.thunderbear06.CCAndroids;
import com.thunderbear06.ai.AndroidBrain;
import com.thunderbear06.component.ComputerComponents;
import com.thunderbear06.computer.peripherals.DummyPocket;
import com.thunderbear06.entity.android.BaseAndroidEntity;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.pocket.IPocketUpgrade;
import dan200.computercraft.api.upgrades.UpgradeData;
import dan200.computercraft.core.computer.ComputerSide;
import dan200.computercraft.shared.ModRegistry;
import dan200.computercraft.shared.computer.core.ComputerFamily;
import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.computer.core.ServerContext;
import dan200.computercraft.shared.computer.inventory.ComputerMenuWithoutInventory;
import dan200.computercraft.shared.config.Config;
import dan200.computercraft.shared.network.container.ComputerContainerData;
import dan200.computercraft.shared.platform.PlatformHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

public class AndroidComputerContainer implements NamedScreenHandlerFactory {
    private final BaseAndroidEntity android;

    public String label = "";
    public boolean on = false;

    @Nullable
    private UUID instanceID = null;
    private int computerID = -1;
    private boolean startOn = false;
    public ComputerFamily family;

    private UpgradeData<IPocketUpgrade> leftUpgrade;
    private UpgradeData<IPocketUpgrade> rightUpgrade;
    private final DummyPocket dummyPocket;

    public AndroidComputerContainer(BaseAndroidEntity android) {
        this.android = android;
        this.dummyPocket = new DummyPocket(this.android);
    }

    public void onTick() {
        if (this.startOn) {
            this.turnOn(getOrCreateServerComputer());
            this.startOn = false;
        }

        if (this.on && this.computerID >= 0) {
            ServerComputer computer = getServerComputer();

            if (computer == null) {
                CCAndroids.LOGGER.error("Android is on but has no associated ServerComputer");
                return;
            }

            this.on = computer.isOn();

            updateOwnerLabel(computer);

            tickPeripherals();

            computer.keepAlive();
        }

        if (!this.on && this.android.isOn) {
            this.android.shutdown();
        }
    }

    public void turnOn(ServerComputer computer) {
        if (!computer.isOn()) {
            computer.turnOn();

            this.computerID = computer.getID();
            this.on = true;
            this.android.isOn = true;

            this.onHandItemChanged(Hand.MAIN_HAND);
            this.onHandItemChanged(Hand.OFF_HAND);

            getUpgradePeripherals();
        }
    }

    public void openComputer(ServerPlayerEntity player) {
        ServerComputer computer = getOrCreateServerComputer();

        if (!this.on) {
            turnOn(computer);
        }

        PlatformHelper.get().openMenu(
                player,
                Text.literal(this.label),
                (syncId, playerInventory, player1) -> new ComputerMenuWithoutInventory(ModRegistry.Menus.COMPUTER.get(), syncId, playerInventory, p -> true, computer),
                new ComputerContainerData(computer, ItemStack.EMPTY)
        );
    }

    protected void updateOwnerLabel(ServerComputer computer) {
        if (!Objects.equals(this.label, computer.getLabel())) {
            this.label = computer.getLabel();

            if (this.label == null || this.label.isEmpty()){
                this.android.setCustomName(Text.empty());
                this.android.setCustomNameVisible(false);
            } else {
                this.android.setCustomName(Text.literal(this.label));
                this.android.setCustomNameVisible(true);
            }
        }
    }

    public final EntityComputer getOrCreateServerComputer() {
        MinecraftServer server = this.android.getWorld().getServer();
        if (server == null) {
            throw new IllegalStateException("Cannot access server computer on the client.");
        } else {
            EntityComputer computer = (EntityComputer) ServerContext.get(server).registry().get(this.instanceID);
            if (computer == null) {
                if (this.computerID < 0) {
                    this.computerID = ComputerCraftAPI.createUniqueNumberedSaveDir(server, "computer");
                }

                computer = this.createComputer(this.computerID);
                this.instanceID = computer.register();
            }

            return computer;
        }
    }

    public void setPeripheral(ComputerSide side, IPeripheral peripheral) {
        ServerComputer computer = getServerComputer();

        if (computer == null) {
            CCAndroids.LOGGER.error("Failed to set peripheral of type {} on side {} of computer container owned by {}. Reason: Failed to get ServerComputer (It was null)", peripheral.getType(), side.getName(), this.android.getName());
            return;
        }

        computer.setPeripheral(side, peripheral);
    }

    public ComputerFamily getFamily() {
        return this.family;
    }

    public int getComputerID() {
        return this.computerID;
    }

    public void setFamily(ComputerFamily family) {
        this.family = family;
    }

    public void setComputerID (int id) {
        this.computerID = id;
    }

    public AndroidBrain getBrain() {
        return this.android.brain;
    }

    protected EntityComputer createComputer(int id) {
        ServerComputer.Properties properties = ServerComputer.properties(id, this.getFamily())
                .addComponent(ComputerComponents.ANDROID_COMPUTER, this.android.brain)
                .label(this.label)
                .terminalSize(Config.DEFAULT_COMPUTER_TERM_WIDTH, Config.DEFAULT_COMPUTER_TERM_HEIGHT);

        return new EntityComputer((ServerWorld)this.android.getWorld(), this.android, properties);
    }

    @Override
    public @Nullable ScreenHandler createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
        return new ComputerMenuWithoutInventory(ModRegistry.Menus.COMPUTER.get(), id, inventory, player1 -> true, this.getOrCreateServerComputer());
    }

    @Nullable
    public ServerComputer getServerComputer() {
        return !this.android.getWorld().isClient && this.android.getWorld().getServer() != null ? ServerContext.get(this.android.getWorld().getServer()).registry().get(this.instanceID) : null;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Android terminal");
    }

    public void writeNbt(NbtCompound computerCompound) {
        computerCompound.putInt("ComputerID", this.getComputerID());
    }

    public void readNbt(NbtCompound computerCompound) {
        this.setComputerID(computerCompound.getInt("ComputerID"));
    }

    public void getUpgradePeripherals() {
        setPeripheral(ComputerSide.LEFT, this.leftUpgrade == null ? null : this.leftUpgrade.upgrade().createPeripheral(this.dummyPocket));
        setPeripheral(ComputerSide.RIGHT, this.rightUpgrade == null ? null : this.rightUpgrade.upgrade().createPeripheral(this.dummyPocket));
    }

    private void tickPeripherals() {
        ServerComputer computer = this.getServerComputer();

        assert computer != null;

        if (this.leftUpgrade != null) leftUpgrade.upgrade().update(this.dummyPocket, computer.getPeripheral(ComputerSide.LEFT));
        if (this.rightUpgrade != null) rightUpgrade.upgrade().update(this.dummyPocket, computer.getPeripheral(ComputerSide.RIGHT));
    }

    public boolean hasUpgrade(ComputerSide side) {
        return (side == ComputerSide.LEFT && this.leftUpgrade != null) || (side == ComputerSide.RIGHT && this.rightUpgrade != null);
    }

    public void onHandItemChanged(Hand hand) {
        ItemStack handItem = this.android.getStackInHand(hand);

        if (hand == Hand.OFF_HAND) {
            this.leftUpgrade = this.dummyPocket.createUpgrade(handItem);
            return;
        }

        this.rightUpgrade = this.dummyPocket.createUpgrade(handItem);
    }
}
