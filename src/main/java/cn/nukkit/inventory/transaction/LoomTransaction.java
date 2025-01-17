package cn.nukkit.inventory.transaction;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.LoomInventory;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.LoomItemAction;
import cn.nukkit.item.Item;

import java.util.List;

public class LoomTransaction extends InventoryTransaction {

    private Item outputItem;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public LoomTransaction(Player source, List<InventoryAction> actions) {
        super(source, actions);
    }

    @Override
    public void addAction(InventoryAction action) {
        super.addAction(action);
        if(action instanceof LoomItemAction) {
            outputItem = action.getSourceItem();
        }
    }

    @Override
    public boolean canExecute() {
        Inventory inventory = getSource().getWindowById(Player.LOOM_WINDOW_ID);
        if (inventory == null) {
            return false;
        }
        LoomInventory loomInventory = (LoomInventory) inventory;

        if(outputItem == null) {
            return false;
        }

        Item first = loomInventory.getFirstItem();
        Item second = loomInventory.getSecondItem();
        if(first.getId() != Item.BANNER || second.getId() != Item.DYE || first.getDamage() != outputItem.getDamage()) {
            return false;
        }
        if(!outputItem.hasCompoundTag()) {
            return false;
        }
        int patternCount = outputItem.getNamedTag().getList("Patterns").size();
        if(first.getNamedTag() == null) {
            return patternCount == 1;
        }

        if(patternCount > 6) {
            return false;
        }

        return first.getNamedTag().getList("Patterns").size() + 1 == patternCount;
    }

    @Override
    public boolean execute() {
        if (this.hasExecuted() || !this.canExecute()) {
            this.source.removeAllWindows(false);
            this.sendInventories();
            return false;
        }

        for (InventoryAction action : this.actions) {
            if (action.execute(this.source)) {
                action.onExecuteSuccess(this.source);
            } else {
                action.onExecuteFail(this.source);
            }
        }
        return true;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static boolean checkForItemPart(List<InventoryAction> actions) {
        return actions.stream().anyMatch(it-> it instanceof LoomItemAction);
    }
}
