package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBarrel;
import cn.nukkit.blockentity.BlockEntityBarrel;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;

import java.util.Iterator;

@PowerNukkitOnly
public class BarrelInventory extends ContainerInventory {

    @PowerNukkitOnly
    public BarrelInventory(BlockEntityBarrel barrel) {
        super(barrel, InventoryType.BARREL);
    }

    @Override
    public BlockEntityBarrel getHolder() {
        return (BlockEntityBarrel) this.holder;
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);

        Iterator<Player> viewerIterator = this.getViewers().iterator();
        Player next = null;
        if (!who.isSpectator() && (this.getViewers().size() == 1 || (this.getViewers().size() == 2 && (next = viewerIterator.next()).equals(who) ? viewerIterator.next().isSpectator() : (next != null && next.isSpectator())))) {
            BlockEntityBarrel barrel = this.getHolder();
            Level level = barrel.getLevel();
            if (level != null) {
                Block block = barrel.getBlock();
                if (block instanceof BlockBarrel) {
                    BlockBarrel blockBarrel = (BlockBarrel) block;
                    if (!blockBarrel.isOpen()) {
                        blockBarrel.setOpen(true);
                        level.setBlock(blockBarrel, blockBarrel, true, true);
                        level.addSound(blockBarrel, Sound.BLOCK_BARREL_OPEN);
                    }
                }
            }
        }
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);

        if (this.getViewers().isEmpty()) {
            BlockEntityBarrel barrel = this.getHolder();
            Level level = barrel.getLevel();
            if (level != null) {
                Block block = barrel.getBlock();
                if (block instanceof BlockBarrel) {
                    BlockBarrel blockBarrel = (BlockBarrel) block;
                    if (blockBarrel.isOpen()) {
                        blockBarrel.setOpen(false);
                        level.setBlock(blockBarrel, blockBarrel, true, true);
                        level.addSound(blockBarrel, Sound.BLOCK_BARREL_CLOSE);
                    }
                }
            }
        }
    }

    @Override
    public boolean canCauseVibration() {
        return true;
    }
}
