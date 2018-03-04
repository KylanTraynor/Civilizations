package com.kylantraynor.civilizations.menus;

import com.kylantraynor.civilizations.banners.BannerOwner;
import com.kylantraynor.civilizations.groups.Group;
import com.kylantraynor.civilizations.managers.ButtonManager;
import com.kylantraynor.civilizations.managers.MenuManager;
import com.kylantraynor.civilizations.utils.Identifier;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GroupExplorer extends Menu {
    private Inventory top;
    public int offset = 0;
    private Group current;
    private Button[] buttons;
    final private MenuReturnFunction<UUID> function;

    public GroupExplorer(Group root, MenuReturnFunction<UUID> function){
        current = root;
        this.function = function;
        initInventory();
    }

    private void initInventory() {
        top = Bukkit.createInventory(null, 6 * 9, ""+ChatColor.BOLD + ChatColor.GOLD+"Select a Group");
    }

    @Override
    public void update() {
        ButtonManager.clearButtons(getPlayer());
        top.clear();
        Group[] groups = getGroupsIn(current);
        Arrays.sort(groups);
        ItemStack up = new ItemStack(Material.GOLD_SPADE, offset);
        top.setItem(8, new Button(getPlayer(), up, "Scroll Up", null, new BukkitRunnable(){
            @Override
            public void run(){
                final GroupExplorer menu = (GroupExplorer) MenuManager.getMenus().get(getPlayer());
                menu.offset--;
                menu.update();
            }
        }, offset > 0));
        ItemStack down = new ItemStack(Material.GOLD_SPADE, getMaxOffset(groups) - offset);
        top.setItem(8 + 9*5, new Button(getPlayer(), down, "Scroll Down", null, new BukkitRunnable(){
            @Override
            public void run(){
                final GroupExplorer menu = (GroupExplorer) MenuManager.getMenus().get(getPlayer());
                menu.offset++;
                menu.update();
            }
        }, offset < getMaxOffset(groups)));
        if(current != null){
            top.setItem(8 + 9*2, getSelectButtonFor(current));
        }
        int pos = 0;
        for(int i = offset * 8; i < groups.length && i < ((offset+6) * 8); i++){
            top.setItem(pos++, getButtonFor(groups[i]));
            if(pos == 8 || pos == 17 || pos == 26 || pos == 35 || pos == 44 || pos == 53){
                pos++;
            }
        }
        getPlayer().updateInventory();
    }

    public int getMaxOffset(Group[] groups){
        return Math.max((groups.length >> 3) - 5, 0);
    }

    public Button getSelectButtonFor(Group g){
        return new Button(getPlayer(), Material.EMERALD_BLOCK, "Select " + g.getName(), null, new BukkitRunnable(){
            @Override
            public void run(){
                function.setReturnedValue(g.getIdentifier());
                function.run();
            }
        }, true);
    }

    public Button getButtonFor(Group g){
        ItemStack appearance = new ItemStack(Material.GOLD_BLOCK);
        if(g instanceof BannerOwner){
            if(((BannerOwner) g).getBanner() != null){
                appearance = ((BannerOwner) g).getBanner().getItemStack();
            }
        }
        return new Button(getPlayer(), appearance, g.getName(), null, new BukkitRunnable(){
            @Override
            public void run(){
                final GroupExplorer menu = (GroupExplorer) MenuManager.getMenus().get(getPlayer());
                menu.current = g;
                menu.offset = 0;
                menu.update();
            }
        }, true);
    }

    public Group[] getGroupsIn(Group root){
        List<Group> result = new ArrayList<>();
        for(Group g : Group.getList()){
            if(root != null){
                if(root.getIdentifier().equals(g.getParentId())){
                    result.add(g);
                }
            } else {
                if(g.getParentId() == null){
                    result.add(g);
                }
            }
        }
        return result.toArray(new Group[result.size()]);
    }

    @Override
    public Inventory getBottomInventory() {
        return getPlayer().getInventory();
    }

    @Override
    public Inventory getTopInventory() {
        return top;
    }

    @Override
    public InventoryType getType() {
        return InventoryType.CHEST;
    }
}
