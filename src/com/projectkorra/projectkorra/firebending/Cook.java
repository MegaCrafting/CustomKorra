/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.entity.Item
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 */
package com.projectkorra.projectkorra.firebending;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.util.ParticleEffect;

public class Cook {
    public static ConcurrentHashMap<Player, Cook> instances = new ConcurrentHashMap();
    private static final long COOK_TIME = 2000;
    private static final Material[] cookables = new Material[]{Material.RAW_BEEF, Material.RAW_CHICKEN, Material.RAW_FISH, Material.PORK, Material.POTATO_ITEM, Material.RABBIT, Material.MUTTON};
    private Player player;
    private ItemStack items;
    private long time;
    private long cooktime = 2000;
    private static /* synthetic */ int[] $SWITCH_TABLE$org$bukkit$Material;

    public Cook(Player player) {
        this.player = player;
        this.items = player.getInventory().getItemInMainHand();
        this.time = System.currentTimeMillis();
        if (Cook.isCookable(this.items.getType())) {
            instances.put(player, this);
        }
    }

    private static boolean isCookable(Material material) {
        return Arrays.asList(cookables).contains((Object)material);
    }

    private void cook() {
        ItemStack cooked = this.getCooked(this.items);
        HashMap cantfit = this.player.getInventory().addItem(new ItemStack[]{cooked});
        Iterator iterator = cantfit.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (Integer)iterator.next();
            this.player.getWorld().dropItem(this.player.getEyeLocation(), (ItemStack)cantfit.get(id));
        }
        int amount = this.items.getAmount();
        if (amount == 1) {
            this.player.getInventory().clear(this.player.getInventory().getHeldItemSlot());
        } else {
            this.items.setAmount(amount - 1);
        }
    }

    private ItemStack getCooked(ItemStack is) {
        ItemStack cooked = new ItemStack(Material.AIR);
        Material material = is.getType();
        switch (Cook.$SWITCH_TABLE$org$bukkit$Material()[material.ordinal()]) {
            case 321: {
                cooked = new ItemStack(Material.COOKED_BEEF, 1);
                break;
            }
            case 307: {
                ItemStack salmon = new ItemStack(Material.RAW_FISH, 1,(short) 1);
                if (is.getDurability() == salmon.getDurability()) {
                    cooked = new ItemStack(Material.COOKED_FISH, 1, (short) 1);
                    break;
                }
                cooked = new ItemStack(Material.COOKED_FISH, 1);
                break;
            }
            case 323: {
                cooked = new ItemStack(Material.COOKED_CHICKEN, 1);
                break;
            }
            case 277: {
                cooked = new ItemStack(Material.GRILLED_PORK, 1);
                break;
            }
            case 350: {
                cooked = new ItemStack(Material.BAKED_POTATO, 1);
                break;
            }
            case 381: {
                cooked = new ItemStack(Material.COOKED_MUTTON);
                break;
            }
            case 369: {
                cooked = new ItemStack(Material.COOKED_RABBIT);
                break;
            }
        }
        return cooked;
    }

    public long getCooktime() {
        return this.cooktime;
    }

    public Player getPlayer() {
        return this.player;
    }

    public long getTime() {
        return this.time;
    }

    public boolean progress() {
        if (this.player.isDead() || !this.player.isOnline()) {
            this.remove();
            return false;
        }
        if (GeneralMethods.getBoundAbility(this.player) == null) {
            this.remove();
            return false;
        }
        if (!this.player.isSneaking() || !GeneralMethods.getBoundAbility(this.player).equalsIgnoreCase("HeatControl")) {
            this.remove();
            return false;
        }
        if (!this.items.equals((Object)this.player.getInventory().getItemInMainHand())) {
            this.time = System.currentTimeMillis();
            this.items = this.player.getInventory().getItemInMainHand();
        }
        if (!Cook.isCookable(this.items.getType())) {
            this.remove();
            return false;
        }
        if (System.currentTimeMillis() > this.time + this.cooktime) {
            this.cook();
            this.time = System.currentTimeMillis();
        }
        ParticleEffect.FLAME.display(this.player.getEyeLocation(), 0.6f, 0.6f, 0.6f, 0.0f, 3);
        ParticleEffect.SMOKE_LARGE.display(this.player.getEyeLocation(), 0.6f, 0.6f, 0.6f, 0.0f, 1);
        return true;
    }

    public static void progressAll() {
        for (Cook ability : instances.values()) {
            ability.progress();
        }
    }

    public void remove() {
        instances.remove((Object)this.player);
    }

    public static void removeAll() {
        for (Cook ability : instances.values()) {
            ability.remove();
        }
    }

    public void setCooktime(long cooktime) {
        this.cooktime = cooktime;
    }

    public void setTime(long time) {
        this.time = time;
    }

    static /* synthetic */ int[] $SWITCH_TABLE$org$bukkit$Material() {
        int[] arrn;
        int[] arrn2 = $SWITCH_TABLE$org$bukkit$Material;
        if (arrn2 != null) {
            return arrn2;
        }
        arrn = new int[Material.values().length];
        try {
            arrn[Material.ACACIA_DOOR.ordinal()] = 197;
        }
        catch (NoSuchFieldError v1) {}
        try {
            arrn[Material.ACACIA_DOOR_ITEM.ordinal()] = 388;
        }
        catch (NoSuchFieldError v2) {}
        try {
            arrn[Material.ACACIA_FENCE.ordinal()] = 193;
        }
        catch (NoSuchFieldError v3) {}
        try {
            arrn[Material.ACACIA_FENCE_GATE.ordinal()] = 188;
        }
        catch (NoSuchFieldError v4) {}
        try {
            arrn[Material.ACACIA_STAIRS.ordinal()] = 164;
        }
        catch (NoSuchFieldError v5) {}
        try {
            arrn[Material.ACTIVATOR_RAIL.ordinal()] = 158;
        }
        catch (NoSuchFieldError v6) {}
        try {
            arrn[Material.AIR.ordinal()] = 1;
        }
        catch (NoSuchFieldError v7) {}
        try {
            arrn[Material.ANVIL.ordinal()] = 146;
        }
        catch (NoSuchFieldError v8) {}
        try {
            arrn[Material.APPLE.ordinal()] = 218;
        }
        catch (NoSuchFieldError v9) {}
        try {
            arrn[Material.ARMOR_STAND.ordinal()] = 374;
        }
        catch (NoSuchFieldError v10) {}
        try {
            arrn[Material.ARROW.ordinal()] = 220;
        }
        catch (NoSuchFieldError v11) {}
        try {
            arrn[Material.BAKED_POTATO.ordinal()] = 351;
        }
        catch (NoSuchFieldError v12) {}
        try {
            arrn[Material.BANNER.ordinal()] = 383;
        }
        catch (NoSuchFieldError v13) {}
        try {
            arrn[Material.BARRIER.ordinal()] = 167;
        }
        catch (NoSuchFieldError v14) {}
        try {
            arrn[Material.BEACON.ordinal()] = 139;
        }
        catch (NoSuchFieldError v15) {}
        try {
            arrn[Material.BED.ordinal()] = 313;
        }
        catch (NoSuchFieldError v16) {}
        try {
            arrn[Material.BEDROCK.ordinal()] = 8;
        }
        catch (NoSuchFieldError v17) {}
        try {
            arrn[Material.BED_BLOCK.ordinal()] = 27;
        }
        catch (NoSuchFieldError v18) {}
        try {
            arrn[Material.BEETROOT.ordinal()] = 392;
        }
        catch (NoSuchFieldError v19) {}
        try {
            arrn[Material.BEETROOT_BLOCK.ordinal()] = 208;
        }
        catch (NoSuchFieldError v20) {}
        try {
            arrn[Material.BEETROOT_SEEDS.ordinal()] = 393;
        }
        catch (NoSuchFieldError v21) {}
        try {
            arrn[Material.BEETROOT_SOUP.ordinal()] = 394;
        }
        catch (NoSuchFieldError v22) {}
        try {
            arrn[Material.BIRCH_DOOR.ordinal()] = 195;
        }
        catch (NoSuchFieldError v23) {}
        try {
            arrn[Material.BIRCH_DOOR_ITEM.ordinal()] = 386;
        }
        catch (NoSuchFieldError v24) {}
        try {
            arrn[Material.BIRCH_FENCE.ordinal()] = 190;
        }
        catch (NoSuchFieldError v25) {}
        try {
            arrn[Material.BIRCH_FENCE_GATE.ordinal()] = 185;
        }
        catch (NoSuchFieldError v26) {}
        try {
            arrn[Material.BIRCH_WOOD_STAIRS.ordinal()] = 136;
        }
        catch (NoSuchFieldError v27) {}
        try {
            arrn[Material.BLAZE_POWDER.ordinal()] = 335;
        }
        catch (NoSuchFieldError v28) {}
        try {
            arrn[Material.BLAZE_ROD.ordinal()] = 327;
        }
        catch (NoSuchFieldError v29) {}
        try {
            arrn[Material.BOAT.ordinal()] = 291;
        }
        catch (NoSuchFieldError v30) {}
        try {
            arrn[Material.BOAT_ACACIA.ordinal()] = 405;
        }
        catch (NoSuchFieldError v31) {}
        try {
            arrn[Material.BOAT_BIRCH.ordinal()] = 403;
        }
        catch (NoSuchFieldError v32) {}
        try {
            arrn[Material.BOAT_DARK_OAK.ordinal()] = 406;
        }
        catch (NoSuchFieldError v33) {}
        try {
            arrn[Material.BOAT_JUNGLE.ordinal()] = 404;
        }
        catch (NoSuchFieldError v34) {}
        try {
            arrn[Material.BOAT_SPRUCE.ordinal()] = 402;
        }
        catch (NoSuchFieldError v35) {}
        try {
            arrn[Material.BONE.ordinal()] = 310;
        }
        catch (NoSuchFieldError v36) {}
        try {
            arrn[Material.BOOK.ordinal()] = 298;
        }
        catch (NoSuchFieldError v37) {}
        try {
            arrn[Material.BOOKSHELF.ordinal()] = 48;
        }
        catch (NoSuchFieldError v38) {}
        try {
            arrn[Material.BOOK_AND_QUILL.ordinal()] = 344;
        }
        catch (NoSuchFieldError v39) {}
        try {
            arrn[Material.BOW.ordinal()] = 219;
        }
        catch (NoSuchFieldError v40) {}
        try {
            arrn[Material.BOWL.ordinal()] = 239;
        }
        catch (NoSuchFieldError v41) {}
        try {
            arrn[Material.BREAD.ordinal()] = 255;
        }
        catch (NoSuchFieldError v42) {}
        try {
            arrn[Material.BREWING_STAND.ordinal()] = 118;
        }
        catch (NoSuchFieldError v43) {}
        try {
            arrn[Material.BREWING_STAND_ITEM.ordinal()] = 337;
        }
        catch (NoSuchFieldError v44) {}
        try {
            arrn[Material.BRICK.ordinal()] = 46;
        }
        catch (NoSuchFieldError v45) {}
        try {
            arrn[Material.BRICK_STAIRS.ordinal()] = 109;
        }
        catch (NoSuchFieldError v46) {}
        try {
            arrn[Material.BROWN_MUSHROOM.ordinal()] = 40;
        }
        catch (NoSuchFieldError v47) {}
        try {
            arrn[Material.BUCKET.ordinal()] = 283;
        }
        catch (NoSuchFieldError v48) {}
        try {
            arrn[Material.BURNING_FURNACE.ordinal()] = 63;
        }
        catch (NoSuchFieldError v49) {}
        try {
            arrn[Material.CACTUS.ordinal()] = 82;
        }
        catch (NoSuchFieldError v50) {}
        try {
            arrn[Material.CAKE.ordinal()] = 312;
        }
        catch (NoSuchFieldError v51) {}
        try {
            arrn[Material.CAKE_BLOCK.ordinal()] = 93;
        }
        catch (NoSuchFieldError v52) {}
        try {
            arrn[Material.CARPET.ordinal()] = 172;
        }
        catch (NoSuchFieldError v53) {}
        try {
            arrn[Material.CARROT.ordinal()] = 142;
        }
        catch (NoSuchFieldError v54) {}
        try {
            arrn[Material.CARROT_ITEM.ordinal()] = 349;
        }
        catch (NoSuchFieldError v55) {}
        try {
            arrn[Material.CARROT_STICK.ordinal()] = 356;
        }
        catch (NoSuchFieldError v56) {}
        try {
            arrn[Material.CAULDRON.ordinal()] = 119;
        }
        catch (NoSuchFieldError v57) {}
        try {
            arrn[Material.CAULDRON_ITEM.ordinal()] = 338;
        }
        catch (NoSuchFieldError v58) {}
        try {
            arrn[Material.CHAINMAIL_BOOTS.ordinal()] = 263;
        }
        catch (NoSuchFieldError v59) {}
        try {
            arrn[Material.CHAINMAIL_CHESTPLATE.ordinal()] = 261;
        }
        catch (NoSuchFieldError v60) {}
        try {
            arrn[Material.CHAINMAIL_HELMET.ordinal()] = 260;
        }
        catch (NoSuchFieldError v61) {}
        try {
            arrn[Material.CHAINMAIL_LEGGINGS.ordinal()] = 262;
        }
        catch (NoSuchFieldError v62) {}
        try {
            arrn[Material.CHEST.ordinal()] = 55;
        }
        catch (NoSuchFieldError v63) {}
        try {
            arrn[Material.CHORUS_FLOWER.ordinal()] = 201;
        }
        catch (NoSuchFieldError v64) {}
        try {
            arrn[Material.CHORUS_FRUIT.ordinal()] = 390;
        }
        catch (NoSuchFieldError v65) {}
        try {
            arrn[Material.CHORUS_FRUIT_POPPED.ordinal()] = 391;
        }
        catch (NoSuchFieldError v66) {}
        try {
            arrn[Material.CHORUS_PLANT.ordinal()] = 200;
        }
        catch (NoSuchFieldError v67) {}
        try {
            arrn[Material.CLAY.ordinal()] = 83;
        }
        catch (NoSuchFieldError v68) {}
        try {
            arrn[Material.CLAY_BALL.ordinal()] = 295;
        }
        catch (NoSuchFieldError v69) {}
        try {
            arrn[Material.CLAY_BRICK.ordinal()] = 294;
        }
        catch (NoSuchFieldError v70) {}
        try {
            arrn[Material.COAL.ordinal()] = 221;
        }
        catch (NoSuchFieldError v71) {}
        try {
            arrn[Material.COAL_BLOCK.ordinal()] = 174;
        }
        catch (NoSuchFieldError v72) {}
        try {
            arrn[Material.COAL_ORE.ordinal()] = 17;
        }
        catch (NoSuchFieldError v73) {}
        try {
            arrn[Material.COBBLESTONE.ordinal()] = 5;
        }
        catch (NoSuchFieldError v74) {}
        try {
            arrn[Material.COBBLESTONE_STAIRS.ordinal()] = 68;
        }
        catch (NoSuchFieldError v75) {}
        try {
            arrn[Material.COBBLE_WALL.ordinal()] = 140;
        }
        catch (NoSuchFieldError v76) {}
        try {
            arrn[Material.COCOA.ordinal()] = 128;
        }
        catch (NoSuchFieldError v77) {}
        try {
            arrn[Material.COMMAND.ordinal()] = 138;
        }
        catch (NoSuchFieldError v78) {}
        try {
            arrn[Material.COMMAND_CHAIN.ordinal()] = 212;
        }
        catch (NoSuchFieldError v79) {}
        try {
            arrn[Material.COMMAND_MINECART.ordinal()] = 380;
        }
        catch (NoSuchFieldError v80) {}
        try {
            arrn[Material.COMMAND_REPEATING.ordinal()] = 211;
        }
        catch (NoSuchFieldError v81) {}
        try {
            arrn[Material.COMPASS.ordinal()] = 303;
        }
        catch (NoSuchFieldError v82) {}
        try {
            arrn[Material.COOKED_BEEF.ordinal()] = 322;
        }
        catch (NoSuchFieldError v83) {}
        try {
            arrn[Material.COOKED_CHICKEN.ordinal()] = 324;
        }
        catch (NoSuchFieldError v84) {}
        try {
            arrn[Material.COOKED_FISH.ordinal()] = 308;
        }
        catch (NoSuchFieldError v85) {}
        try {
            arrn[Material.COOKED_MUTTON.ordinal()] = 382;
        }
        catch (NoSuchFieldError v86) {}
        try {
            arrn[Material.COOKED_RABBIT.ordinal()] = 370;
        }
        catch (NoSuchFieldError v87) {}
        try {
            arrn[Material.COOKIE.ordinal()] = 315;
        }
        catch (NoSuchFieldError v88) {}
        try {
            arrn[Material.CROPS.ordinal()] = 60;
        }
        catch (NoSuchFieldError v89) {}
        try {
            arrn[Material.DARK_OAK_DOOR.ordinal()] = 198;
        }
        catch (NoSuchFieldError v90) {}
        try {
            arrn[Material.DARK_OAK_DOOR_ITEM.ordinal()] = 389;
        }
        catch (NoSuchFieldError v91) {}
        try {
            arrn[Material.DARK_OAK_FENCE.ordinal()] = 192;
        }
        catch (NoSuchFieldError v92) {}
        try {
            arrn[Material.DARK_OAK_FENCE_GATE.ordinal()] = 187;
        }
        catch (NoSuchFieldError v93) {}
        try {
            arrn[Material.DARK_OAK_STAIRS.ordinal()] = 165;
        }
        catch (NoSuchFieldError v94) {}
        try {
            arrn[Material.DAYLIGHT_DETECTOR.ordinal()] = 152;
        }
        catch (NoSuchFieldError v95) {}
        try {
            arrn[Material.DAYLIGHT_DETECTOR_INVERTED.ordinal()] = 179;
        }
        catch (NoSuchFieldError v96) {}
        try {
            arrn[Material.DEAD_BUSH.ordinal()] = 33;
        }
        catch (NoSuchFieldError v97) {}
        try {
            arrn[Material.DETECTOR_RAIL.ordinal()] = 29;
        }
        catch (NoSuchFieldError v98) {}
        try {
            arrn[Material.DIAMOND.ordinal()] = 222;
        }
        catch (NoSuchFieldError v99) {}
        try {
            arrn[Material.DIAMOND_AXE.ordinal()] = 237;
        }
        catch (NoSuchFieldError v100) {}
        try {
            arrn[Material.DIAMOND_BARDING.ordinal()] = 377;
        }
        catch (NoSuchFieldError v101) {}
        try {
            arrn[Material.DIAMOND_BLOCK.ordinal()] = 58;
        }
        catch (NoSuchFieldError v102) {}
        try {
            arrn[Material.DIAMOND_BOOTS.ordinal()] = 271;
        }
        catch (NoSuchFieldError v103) {}
        try {
            arrn[Material.DIAMOND_CHESTPLATE.ordinal()] = 269;
        }
        catch (NoSuchFieldError v104) {}
        try {
            arrn[Material.DIAMOND_HELMET.ordinal()] = 268;
        }
        catch (NoSuchFieldError v105) {}
        try {
            arrn[Material.DIAMOND_HOE.ordinal()] = 251;
        }
        catch (NoSuchFieldError v106) {}
        try {
            arrn[Material.DIAMOND_LEGGINGS.ordinal()] = 270;
        }
        catch (NoSuchFieldError v107) {}
        try {
            arrn[Material.DIAMOND_ORE.ordinal()] = 57;
        }
        catch (NoSuchFieldError v108) {}
        try {
            arrn[Material.DIAMOND_PICKAXE.ordinal()] = 236;
        }
        catch (NoSuchFieldError v109) {}
        try {
            arrn[Material.DIAMOND_SPADE.ordinal()] = 235;
        }
        catch (NoSuchFieldError v110) {}
        try {
            arrn[Material.DIAMOND_SWORD.ordinal()] = 234;
        }
        catch (NoSuchFieldError v111) {}
        try {
            arrn[Material.DIODE.ordinal()] = 314;
        }
        catch (NoSuchFieldError v112) {}
        try {
            arrn[Material.DIODE_BLOCK_OFF.ordinal()] = 94;
        }
        catch (NoSuchFieldError v113) {}
        try {
            arrn[Material.DIODE_BLOCK_ON.ordinal()] = 95;
        }
        catch (NoSuchFieldError v114) {}
        try {
            arrn[Material.DIRT.ordinal()] = 4;
        }
        catch (NoSuchFieldError v115) {}
        try {
            arrn[Material.DISPENSER.ordinal()] = 24;
        }
        catch (NoSuchFieldError v116) {}
        try {
            arrn[Material.DOUBLE_PLANT.ordinal()] = 176;
        }
        catch (NoSuchFieldError v117) {}
        try {
            arrn[Material.DOUBLE_STEP.ordinal()] = 44;
        }
        catch (NoSuchFieldError v118) {}
        try {
            arrn[Material.DOUBLE_STONE_SLAB2.ordinal()] = 182;
        }
        catch (NoSuchFieldError v119) {}
        try {
            arrn[Material.DRAGONS_BREATH.ordinal()] = 395;
        }
        catch (NoSuchFieldError v120) {}
        try {
            arrn[Material.DRAGON_EGG.ordinal()] = 123;
        }
        catch (NoSuchFieldError v121) {}
        try {
            arrn[Material.DROPPER.ordinal()] = 159;
        }
        catch (NoSuchFieldError v122) {}
        try {
            arrn[Material.EGG.ordinal()] = 302;
        }
        catch (NoSuchFieldError v123) {}
        try {
            arrn[Material.ELYTRA.ordinal()] = 401;
        }
        catch (NoSuchFieldError v124) {}
        try {
            arrn[Material.EMERALD.ordinal()] = 346;
        }
        catch (NoSuchFieldError v125) {}
        try {
            arrn[Material.EMERALD_BLOCK.ordinal()] = 134;
        }
        catch (NoSuchFieldError v126) {}
        try {
            arrn[Material.EMERALD_ORE.ordinal()] = 130;
        }
        catch (NoSuchFieldError v127) {}
        try {
            arrn[Material.EMPTY_MAP.ordinal()] = 353;
        }
        catch (NoSuchFieldError v128) {}
        try {
            arrn[Material.ENCHANTED_BOOK.ordinal()] = 361;
        }
        catch (NoSuchFieldError v129) {}
        try {
            arrn[Material.ENCHANTMENT_TABLE.ordinal()] = 117;
        }
        catch (NoSuchFieldError v130) {}
        try {
            arrn[Material.ENDER_CHEST.ordinal()] = 131;
        }
        catch (NoSuchFieldError v131) {}
        try {
            arrn[Material.ENDER_PEARL.ordinal()] = 326;
        }
        catch (NoSuchFieldError v132) {}
        try {
            arrn[Material.ENDER_PORTAL.ordinal()] = 120;
        }
        catch (NoSuchFieldError v133) {}
        try {
            arrn[Material.ENDER_PORTAL_FRAME.ordinal()] = 121;
        }
        catch (NoSuchFieldError v134) {}
        try {
            arrn[Material.ENDER_STONE.ordinal()] = 122;
        }
        catch (NoSuchFieldError v135) {}
        try {
            arrn[Material.END_BRICKS.ordinal()] = 207;
        }
        catch (NoSuchFieldError v136) {}
        try {
            arrn[Material.END_CRYSTAL.ordinal()] = 384;
        }
        catch (NoSuchFieldError v137) {}
        try {
            arrn[Material.END_GATEWAY.ordinal()] = 210;
        }
        catch (NoSuchFieldError v138) {}
        try {
            arrn[Material.END_ROD.ordinal()] = 199;
        }
        catch (NoSuchFieldError v139) {}
        try {
            arrn[Material.EXPLOSIVE_MINECART.ordinal()] = 365;
        }
        catch (NoSuchFieldError v140) {}
        try {
            arrn[Material.EXP_BOTTLE.ordinal()] = 342;
        }
        catch (NoSuchFieldError v141) {}
        try {
            arrn[Material.EYE_OF_ENDER.ordinal()] = 339;
        }
        catch (NoSuchFieldError v142) {}
        try {
            arrn[Material.FEATHER.ordinal()] = 246;
        }
        catch (NoSuchFieldError v143) {}
        try {
            arrn[Material.FENCE.ordinal()] = 86;
        }
        catch (NoSuchFieldError v144) {}
        try {
            arrn[Material.FENCE_GATE.ordinal()] = 108;
        }
        catch (NoSuchFieldError v145) {}
        try {
            arrn[Material.FERMENTED_SPIDER_EYE.ordinal()] = 334;
        }
        catch (NoSuchFieldError v146) {}
        try {
            arrn[Material.FIRE.ordinal()] = 52;
        }
        catch (NoSuchFieldError v147) {}
        try {
            arrn[Material.FIREBALL.ordinal()] = 343;
        }
        catch (NoSuchFieldError v148) {}
        try {
            arrn[Material.FIREWORK.ordinal()] = 359;
        }
        catch (NoSuchFieldError v149) {}
        try {
            arrn[Material.FIREWORK_CHARGE.ordinal()] = 360;
        }
        catch (NoSuchFieldError v150) {}
        try {
            arrn[Material.FISHING_ROD.ordinal()] = 304;
        }
        catch (NoSuchFieldError v151) {}
        try {
            arrn[Material.FLINT.ordinal()] = 276;
        }
        catch (NoSuchFieldError v152) {}
        try {
            arrn[Material.FLINT_AND_STEEL.ordinal()] = 217;
        }
        catch (NoSuchFieldError v153) {}
        try {
            arrn[Material.FLOWER_POT.ordinal()] = 141;
        }
        catch (NoSuchFieldError v154) {}
        try {
            arrn[Material.FLOWER_POT_ITEM.ordinal()] = 348;
        }
        catch (NoSuchFieldError v155) {}
        try {
            arrn[Material.FURNACE.ordinal()] = 62;
        }
        catch (NoSuchFieldError v156) {}
        try {
            arrn[Material.GHAST_TEAR.ordinal()] = 328;
        }
        catch (NoSuchFieldError v157) {}
        try {
            arrn[Material.GLASS.ordinal()] = 21;
        }
        catch (NoSuchFieldError v158) {}
        try {
            arrn[Material.GLASS_BOTTLE.ordinal()] = 332;
        }
        catch (NoSuchFieldError v159) {}
        try {
            arrn[Material.GLOWING_REDSTONE_ORE.ordinal()] = 75;
        }
        catch (NoSuchFieldError v160) {}
        try {
            arrn[Material.GLOWSTONE.ordinal()] = 90;
        }
        catch (NoSuchFieldError v161) {}
        try {
            arrn[Material.GLOWSTONE_DUST.ordinal()] = 306;
        }
        catch (NoSuchFieldError v162) {}
        try {
            arrn[Material.GOLDEN_APPLE.ordinal()] = 280;
        }
        catch (NoSuchFieldError v163) {}
        try {
            arrn[Material.GOLDEN_CARROT.ordinal()] = 354;
        }
        catch (NoSuchFieldError v164) {}
        try {
            arrn[Material.GOLD_AXE.ordinal()] = 244;
        }
        catch (NoSuchFieldError v165) {}
        try {
            arrn[Material.GOLD_BARDING.ordinal()] = 376;
        }
        catch (NoSuchFieldError v166) {}
        try {
            arrn[Material.GOLD_BLOCK.ordinal()] = 42;
        }
        catch (NoSuchFieldError v167) {}
        try {
            arrn[Material.GOLD_BOOTS.ordinal()] = 275;
        }
        catch (NoSuchFieldError v168) {}
        try {
            arrn[Material.GOLD_CHESTPLATE.ordinal()] = 273;
        }
        catch (NoSuchFieldError v169) {}
        try {
            arrn[Material.GOLD_HELMET.ordinal()] = 272;
        }
        catch (NoSuchFieldError v170) {}
        try {
            arrn[Material.GOLD_HOE.ordinal()] = 252;
        }
        catch (NoSuchFieldError v171) {}
        try {
            arrn[Material.GOLD_INGOT.ordinal()] = 224;
        }
        catch (NoSuchFieldError v172) {}
        try {
            arrn[Material.GOLD_LEGGINGS.ordinal()] = 274;
        }
        catch (NoSuchFieldError v173) {}
        try {
            arrn[Material.GOLD_NUGGET.ordinal()] = 329;
        }
        catch (NoSuchFieldError v174) {}
        try {
            arrn[Material.GOLD_ORE.ordinal()] = 15;
        }
        catch (NoSuchFieldError v175) {}
        try {
            arrn[Material.GOLD_PICKAXE.ordinal()] = 243;
        }
        catch (NoSuchFieldError v176) {}
        try {
            arrn[Material.GOLD_PLATE.ordinal()] = 148;
        }
        catch (NoSuchFieldError v177) {}
        try {
            arrn[Material.GOLD_RECORD.ordinal()] = 407;
        }
        catch (NoSuchFieldError v178) {}
        try {
            arrn[Material.GOLD_SPADE.ordinal()] = 242;
        }
        catch (NoSuchFieldError v179) {}
        try {
            arrn[Material.GOLD_SWORD.ordinal()] = 241;
        }
        catch (NoSuchFieldError v180) {}
        try {
            arrn[Material.GRASS.ordinal()] = 3;
        }
        catch (NoSuchFieldError v181) {}
        try {
            arrn[Material.GRASS_PATH.ordinal()] = 209;
        }
        catch (NoSuchFieldError v182) {}
        try {
            arrn[Material.GRAVEL.ordinal()] = 14;
        }
        catch (NoSuchFieldError v183) {}
        try {
            arrn[Material.GREEN_RECORD.ordinal()] = 408;
        }
        catch (NoSuchFieldError v184) {}
        try {
            arrn[Material.GRILLED_PORK.ordinal()] = 278;
        }
        catch (NoSuchFieldError v185) {}
        try {
            arrn[Material.HARD_CLAY.ordinal()] = 173;
        }
        catch (NoSuchFieldError v186) {}
        try {
            arrn[Material.HAY_BLOCK.ordinal()] = 171;
        }
        catch (NoSuchFieldError v187) {}
        try {
            arrn[Material.HOPPER.ordinal()] = 155;
        }
        catch (NoSuchFieldError v188) {}
        try {
            arrn[Material.HOPPER_MINECART.ordinal()] = 366;
        }
        catch (NoSuchFieldError v189) {}
        try {
            arrn[Material.HUGE_MUSHROOM_1.ordinal()] = 100;
        }
        catch (NoSuchFieldError v190) {}
        try {
            arrn[Material.HUGE_MUSHROOM_2.ordinal()] = 101;
        }
        catch (NoSuchFieldError v191) {}
        try {
            arrn[Material.ICE.ordinal()] = 80;
        }
        catch (NoSuchFieldError v192) {}
        try {
            arrn[Material.INK_SACK.ordinal()] = 309;
        }
        catch (NoSuchFieldError v193) {}
        try {
            arrn[Material.IRON_AXE.ordinal()] = 216;
        }
        catch (NoSuchFieldError v194) {}
        try {
            arrn[Material.IRON_BARDING.ordinal()] = 375;
        }
        catch (NoSuchFieldError v195) {}
        try {
            arrn[Material.IRON_BLOCK.ordinal()] = 43;
        }
        catch (NoSuchFieldError v196) {}
        try {
            arrn[Material.IRON_BOOTS.ordinal()] = 267;
        }
        catch (NoSuchFieldError v197) {}
        try {
            arrn[Material.IRON_CHESTPLATE.ordinal()] = 265;
        }
        catch (NoSuchFieldError v198) {}
        try {
            arrn[Material.IRON_DOOR.ordinal()] = 288;
        }
        catch (NoSuchFieldError v199) {}
        try {
            arrn[Material.IRON_DOOR_BLOCK.ordinal()] = 72;
        }
        catch (NoSuchFieldError v200) {}
        try {
            arrn[Material.IRON_FENCE.ordinal()] = 102;
        }
        catch (NoSuchFieldError v201) {}
        try {
            arrn[Material.IRON_HELMET.ordinal()] = 264;
        }
        catch (NoSuchFieldError v202) {}
        try {
            arrn[Material.IRON_HOE.ordinal()] = 250;
        }
        catch (NoSuchFieldError v203) {}
        try {
            arrn[Material.IRON_INGOT.ordinal()] = 223;
        }
        catch (NoSuchFieldError v204) {}
        try {
            arrn[Material.IRON_LEGGINGS.ordinal()] = 266;
        }
        catch (NoSuchFieldError v205) {}
        try {
            arrn[Material.IRON_ORE.ordinal()] = 16;
        }
        catch (NoSuchFieldError v206) {}
        try {
            arrn[Material.IRON_PICKAXE.ordinal()] = 215;
        }
        catch (NoSuchFieldError v207) {}
        try {
            arrn[Material.IRON_PLATE.ordinal()] = 149;
        }
        catch (NoSuchFieldError v208) {}
        try {
            arrn[Material.IRON_SPADE.ordinal()] = 214;
        }
        catch (NoSuchFieldError v209) {}
        try {
            arrn[Material.IRON_SWORD.ordinal()] = 225;
        }
        catch (NoSuchFieldError v210) {}
        try {
            arrn[Material.IRON_TRAPDOOR.ordinal()] = 168;
        }
        catch (NoSuchFieldError v211) {}
        try {
            arrn[Material.ITEM_FRAME.ordinal()] = 347;
        }
        catch (NoSuchFieldError v212) {}
        try {
            arrn[Material.JACK_O_LANTERN.ordinal()] = 92;
        }
        catch (NoSuchFieldError v213) {}
        try {
            arrn[Material.JUKEBOX.ordinal()] = 85;
        }
        catch (NoSuchFieldError v214) {}
        try {
            arrn[Material.JUNGLE_DOOR.ordinal()] = 196;
        }
        catch (NoSuchFieldError v215) {}
        try {
            arrn[Material.JUNGLE_DOOR_ITEM.ordinal()] = 387;
        }
        catch (NoSuchFieldError v216) {}
        try {
            arrn[Material.JUNGLE_FENCE.ordinal()] = 191;
        }
        catch (NoSuchFieldError v217) {}
        try {
            arrn[Material.JUNGLE_FENCE_GATE.ordinal()] = 186;
        }
        catch (NoSuchFieldError v218) {}
        try {
            arrn[Material.JUNGLE_WOOD_STAIRS.ordinal()] = 137;
        }
        catch (NoSuchFieldError v219) {}
        try {
            arrn[Material.LADDER.ordinal()] = 66;
        }
        catch (NoSuchFieldError v220) {}
        try {
            arrn[Material.LAPIS_BLOCK.ordinal()] = 23;
        }
        catch (NoSuchFieldError v221) {}
        try {
            arrn[Material.LAPIS_ORE.ordinal()] = 22;
        }
        catch (NoSuchFieldError v222) {}
        try {
            arrn[Material.LAVA.ordinal()] = 11;
        }
        catch (NoSuchFieldError v223) {}
        try {
            arrn[Material.LAVA_BUCKET.ordinal()] = 285;
        }
        catch (NoSuchFieldError v224) {}
        try {
            arrn[Material.LEASH.ordinal()] = 378;
        }
        catch (NoSuchFieldError v225) {}
        try {
            arrn[Material.LEATHER.ordinal()] = 292;
        }
        catch (NoSuchFieldError v226) {}
        try {
            arrn[Material.LEATHER_BOOTS.ordinal()] = 259;
        }
        catch (NoSuchFieldError v227) {}
        try {
            arrn[Material.LEATHER_CHESTPLATE.ordinal()] = 257;
        }
        catch (NoSuchFieldError v228) {}
        try {
            arrn[Material.LEATHER_HELMET.ordinal()] = 256;
        }
        catch (NoSuchFieldError v229) {}
        try {
            arrn[Material.LEATHER_LEGGINGS.ordinal()] = 258;
        }
        catch (NoSuchFieldError v230) {}
        try {
            arrn[Material.LEAVES.ordinal()] = 19;
        }
        catch (NoSuchFieldError v231) {}
        try {
            arrn[Material.LEAVES_2.ordinal()] = 162;
        }
        catch (NoSuchFieldError v232) {}
        try {
            arrn[Material.LEVER.ordinal()] = 70;
        }
        catch (NoSuchFieldError v233) {}
        try {
            arrn[Material.LINGERING_POTION.ordinal()] = 399;
        }
        catch (NoSuchFieldError v234) {}
        try {
            arrn[Material.LOG.ordinal()] = 18;
        }
        catch (NoSuchFieldError v235) {}
        try {
            arrn[Material.LOG_2.ordinal()] = 163;
        }
        catch (NoSuchFieldError v236) {}
        try {
            arrn[Material.LONG_GRASS.ordinal()] = 32;
        }
        catch (NoSuchFieldError v237) {}
        try {
            arrn[Material.MAGMA_CREAM.ordinal()] = 336;
        }
        catch (NoSuchFieldError v238) {}
        try {
            arrn[Material.MAP.ordinal()] = 316;
        }
        catch (NoSuchFieldError v239) {}
        try {
            arrn[Material.MELON.ordinal()] = 318;
        }
        catch (NoSuchFieldError v240) {}
        try {
            arrn[Material.MELON_BLOCK.ordinal()] = 104;
        }
        catch (NoSuchFieldError v241) {}
        try {
            arrn[Material.MELON_SEEDS.ordinal()] = 320;
        }
        catch (NoSuchFieldError v242) {}
        try {
            arrn[Material.MELON_STEM.ordinal()] = 106;
        }
        catch (NoSuchFieldError v243) {}
        try {
            arrn[Material.MILK_BUCKET.ordinal()] = 293;
        }
        catch (NoSuchFieldError v244) {}
        try {
            arrn[Material.MINECART.ordinal()] = 286;
        }
        catch (NoSuchFieldError v245) {}
        try {
            arrn[Material.MOB_SPAWNER.ordinal()] = 53;
        }
        catch (NoSuchFieldError v246) {}
        try {
            arrn[Material.MONSTER_EGG.ordinal()] = 341;
        }
        catch (NoSuchFieldError v247) {}
        try {
            arrn[Material.MONSTER_EGGS.ordinal()] = 98;
        }
        catch (NoSuchFieldError v248) {}
        try {
            arrn[Material.MOSSY_COBBLESTONE.ordinal()] = 49;
        }
        catch (NoSuchFieldError v249) {}
        try {
            arrn[Material.MUSHROOM_SOUP.ordinal()] = 240;
        }
        catch (NoSuchFieldError v250) {}
        try {
            arrn[Material.MUTTON.ordinal()] = 381;
        }
        catch (NoSuchFieldError v251) {}
        try {
            arrn[Material.MYCEL.ordinal()] = 111;
        }
        catch (NoSuchFieldError v252) {}
        try {
            arrn[Material.NAME_TAG.ordinal()] = 379;
        }
        catch (NoSuchFieldError v253) {}
        try {
            arrn[Material.NETHERRACK.ordinal()] = 88;
        }
        catch (NoSuchFieldError v254) {}
        try {
            arrn[Material.NETHER_BRICK.ordinal()] = 113;
        }
        catch (NoSuchFieldError v255) {}
        try {
            arrn[Material.NETHER_BRICK_ITEM.ordinal()] = 363;
        }
        catch (NoSuchFieldError v256) {}
        try {
            arrn[Material.NETHER_BRICK_STAIRS.ordinal()] = 115;
        }
        catch (NoSuchFieldError v257) {}
        try {
            arrn[Material.NETHER_FENCE.ordinal()] = 114;
        }
        catch (NoSuchFieldError v258) {}
        try {
            arrn[Material.NETHER_STALK.ordinal()] = 330;
        }
        catch (NoSuchFieldError v259) {}
        try {
            arrn[Material.NETHER_STAR.ordinal()] = 357;
        }
        catch (NoSuchFieldError v260) {}
        try {
            arrn[Material.NETHER_WARTS.ordinal()] = 116;
        }
        catch (NoSuchFieldError v261) {}
        try {
            arrn[Material.NOTE_BLOCK.ordinal()] = 26;
        }
        catch (NoSuchFieldError v262) {}
        try {
            arrn[Material.OBSIDIAN.ordinal()] = 50;
        }
        catch (NoSuchFieldError v263) {}
        try {
            arrn[Material.PACKED_ICE.ordinal()] = 175;
        }
        catch (NoSuchFieldError v264) {}
        try {
            arrn[Material.PAINTING.ordinal()] = 279;
        }
        catch (NoSuchFieldError v265) {}
        try {
            arrn[Material.PAPER.ordinal()] = 297;
        }
        catch (NoSuchFieldError v266) {}
        try {
            arrn[Material.PISTON_BASE.ordinal()] = 34;
        }
        catch (NoSuchFieldError v267) {}
        try {
            arrn[Material.PISTON_EXTENSION.ordinal()] = 35;
        }
        catch (NoSuchFieldError v268) {}
        try {
            arrn[Material.PISTON_MOVING_PIECE.ordinal()] = 37;
        }
        catch (NoSuchFieldError v269) {}
        try {
            arrn[Material.PISTON_STICKY_BASE.ordinal()] = 30;
        }
        catch (NoSuchFieldError v270) {}
        try {
            arrn[Material.POISONOUS_POTATO.ordinal()] = 352;
        }
        catch (NoSuchFieldError v271) {}
        try {
            arrn[Material.PORK.ordinal()] = 277;
        }
        catch (NoSuchFieldError v272) {}
        try {
            arrn[Material.PORTAL.ordinal()] = 91;
        }
        catch (NoSuchFieldError v273) {}
        try {
            arrn[Material.POTATO.ordinal()] = 143;
        }
        catch (NoSuchFieldError v274) {}
        try {
            arrn[Material.POTATO_ITEM.ordinal()] = 350;
        }
        catch (NoSuchFieldError v275) {}
        try {
            arrn[Material.POTION.ordinal()] = 331;
        }
        catch (NoSuchFieldError v276) {}
        try {
            arrn[Material.POWERED_MINECART.ordinal()] = 301;
        }
        catch (NoSuchFieldError v277) {}
        try {
            arrn[Material.POWERED_RAIL.ordinal()] = 28;
        }
        catch (NoSuchFieldError v278) {}
        try {
            arrn[Material.PRISMARINE.ordinal()] = 169;
        }
        catch (NoSuchFieldError v279) {}
        try {
            arrn[Material.PRISMARINE_CRYSTALS.ordinal()] = 368;
        }
        catch (NoSuchFieldError v280) {}
        try {
            arrn[Material.PRISMARINE_SHARD.ordinal()] = 367;
        }
        catch (NoSuchFieldError v281) {}
        try {
            arrn[Material.PUMPKIN.ordinal()] = 87;
        }
        catch (NoSuchFieldError v282) {}
        try {
            arrn[Material.PUMPKIN_PIE.ordinal()] = 358;
        }
        catch (NoSuchFieldError v283) {}
        try {
            arrn[Material.PUMPKIN_SEEDS.ordinal()] = 319;
        }
        catch (NoSuchFieldError v284) {}
        try {
            arrn[Material.PUMPKIN_STEM.ordinal()] = 105;
        }
        catch (NoSuchFieldError v285) {}
        try {
            arrn[Material.PURPUR_BLOCK.ordinal()] = 202;
        }
        catch (NoSuchFieldError v286) {}
        try {
            arrn[Material.PURPUR_DOUBLE_SLAB.ordinal()] = 205;
        }
        catch (NoSuchFieldError v287) {}
        try {
            arrn[Material.PURPUR_PILLAR.ordinal()] = 203;
        }
        catch (NoSuchFieldError v288) {}
        try {
            arrn[Material.PURPUR_SLAB.ordinal()] = 206;
        }
        catch (NoSuchFieldError v289) {}
        try {
            arrn[Material.PURPUR_STAIRS.ordinal()] = 204;
        }
        catch (NoSuchFieldError v290) {}
        try {
            arrn[Material.QUARTZ.ordinal()] = 364;
        }
        catch (NoSuchFieldError v291) {}
        try {
            arrn[Material.QUARTZ_BLOCK.ordinal()] = 156;
        }
        catch (NoSuchFieldError v292) {}
        try {
            arrn[Material.QUARTZ_ORE.ordinal()] = 154;
        }
        catch (NoSuchFieldError v293) {}
        try {
            arrn[Material.QUARTZ_STAIRS.ordinal()] = 157;
        }
        catch (NoSuchFieldError v294) {}
        try {
            arrn[Material.RABBIT.ordinal()] = 369;
        }
        catch (NoSuchFieldError v295) {}
        try {
            arrn[Material.RABBIT_FOOT.ordinal()] = 372;
        }
        catch (NoSuchFieldError v296) {}
        try {
            arrn[Material.RABBIT_HIDE.ordinal()] = 373;
        }
        catch (NoSuchFieldError v297) {}
        try {
            arrn[Material.RABBIT_STEW.ordinal()] = 371;
        }
        catch (NoSuchFieldError v298) {}
        try {
            arrn[Material.RAILS.ordinal()] = 67;
        }
        catch (NoSuchFieldError v299) {}
        try {
            arrn[Material.RAW_BEEF.ordinal()] = 321;
        }
        catch (NoSuchFieldError v300) {}
        try {
            arrn[Material.RAW_CHICKEN.ordinal()] = 323;
        }
        catch (NoSuchFieldError v301) {}
        try {
            arrn[Material.RAW_FISH.ordinal()] = 307;
        }
        catch (NoSuchFieldError v302) {}
        try {
            arrn[Material.RECORD_10.ordinal()] = 416;
        }
        catch (NoSuchFieldError v303) {}
        try {
            arrn[Material.RECORD_11.ordinal()] = 417;
        }
        catch (NoSuchFieldError v304) {}
        try {
            arrn[Material.RECORD_12.ordinal()] = 418;
        }
        catch (NoSuchFieldError v305) {}
        try {
            arrn[Material.RECORD_3.ordinal()] = 409;
        }
        catch (NoSuchFieldError v306) {}
        try {
            arrn[Material.RECORD_4.ordinal()] = 410;
        }
        catch (NoSuchFieldError v307) {}
        try {
            arrn[Material.RECORD_5.ordinal()] = 411;
        }
        catch (NoSuchFieldError v308) {}
        try {
            arrn[Material.RECORD_6.ordinal()] = 412;
        }
        catch (NoSuchFieldError v309) {}
        try {
            arrn[Material.RECORD_7.ordinal()] = 413;
        }
        catch (NoSuchFieldError v310) {}
        try {
            arrn[Material.RECORD_8.ordinal()] = 414;
        }
        catch (NoSuchFieldError v311) {}
        try {
            arrn[Material.RECORD_9.ordinal()] = 415;
        }
        catch (NoSuchFieldError v312) {}
        try {
            arrn[Material.REDSTONE.ordinal()] = 289;
        }
        catch (NoSuchFieldError v313) {}
        try {
            arrn[Material.REDSTONE_BLOCK.ordinal()] = 153;
        }
        catch (NoSuchFieldError v314) {}
        try {
            arrn[Material.REDSTONE_COMPARATOR.ordinal()] = 362;
        }
        catch (NoSuchFieldError v315) {}
        try {
            arrn[Material.REDSTONE_COMPARATOR_OFF.ordinal()] = 150;
        }
        catch (NoSuchFieldError v316) {}
        try {
            arrn[Material.REDSTONE_COMPARATOR_ON.ordinal()] = 151;
        }
        catch (NoSuchFieldError v317) {}
        try {
            arrn[Material.REDSTONE_LAMP_OFF.ordinal()] = 124;
        }
        catch (NoSuchFieldError v318) {}
        try {
            arrn[Material.REDSTONE_LAMP_ON.ordinal()] = 125;
        }
        catch (NoSuchFieldError v319) {}
        try {
            arrn[Material.REDSTONE_ORE.ordinal()] = 74;
        }
        catch (NoSuchFieldError v320) {}
        try {
            arrn[Material.REDSTONE_TORCH_OFF.ordinal()] = 76;
        }
        catch (NoSuchFieldError v321) {}
        try {
            arrn[Material.REDSTONE_TORCH_ON.ordinal()] = 77;
        }
        catch (NoSuchFieldError v322) {}
        try {
            arrn[Material.REDSTONE_WIRE.ordinal()] = 56;
        }
        catch (NoSuchFieldError v323) {}
        try {
            arrn[Material.RED_MUSHROOM.ordinal()] = 41;
        }
        catch (NoSuchFieldError v324) {}
        try {
            arrn[Material.RED_ROSE.ordinal()] = 39;
        }
        catch (NoSuchFieldError v325) {}
        try {
            arrn[Material.RED_SANDSTONE.ordinal()] = 180;
        }
        catch (NoSuchFieldError v326) {}
        try {
            arrn[Material.RED_SANDSTONE_STAIRS.ordinal()] = 181;
        }
        catch (NoSuchFieldError v327) {}
        try {
            arrn[Material.ROTTEN_FLESH.ordinal()] = 325;
        }
        catch (NoSuchFieldError v328) {}
        try {
            arrn[Material.SADDLE.ordinal()] = 287;
        }
        catch (NoSuchFieldError v329) {}
        try {
            arrn[Material.SAND.ordinal()] = 13;
        }
        catch (NoSuchFieldError v330) {}
        try {
            arrn[Material.SANDSTONE.ordinal()] = 25;
        }
        catch (NoSuchFieldError v331) {}
        try {
            arrn[Material.SANDSTONE_STAIRS.ordinal()] = 129;
        }
        catch (NoSuchFieldError v332) {}
        try {
            arrn[Material.SAPLING.ordinal()] = 7;
        }
        catch (NoSuchFieldError v333) {}
        try {
            arrn[Material.SEA_LANTERN.ordinal()] = 170;
        }
        catch (NoSuchFieldError v334) {}
        try {
            arrn[Material.SEEDS.ordinal()] = 253;
        }
        catch (NoSuchFieldError v335) {}
        try {
            arrn[Material.SHEARS.ordinal()] = 317;
        }
        catch (NoSuchFieldError v336) {}
        try {
            arrn[Material.SHIELD.ordinal()] = 400;
        }
        catch (NoSuchFieldError v337) {}
        try {
            arrn[Material.SIGN.ordinal()] = 281;
        }
        catch (NoSuchFieldError v338) {}
        try {
            arrn[Material.SIGN_POST.ordinal()] = 64;
        }
        catch (NoSuchFieldError v339) {}
        try {
            arrn[Material.SKULL.ordinal()] = 145;
        }
        catch (NoSuchFieldError v340) {}
        try {
            arrn[Material.SKULL_ITEM.ordinal()] = 355;
        }
        catch (NoSuchFieldError v341) {}
        try {
            arrn[Material.SLIME_BALL.ordinal()] = 299;
        }
        catch (NoSuchFieldError v342) {}
        try {
            arrn[Material.SLIME_BLOCK.ordinal()] = 166;
        }
        catch (NoSuchFieldError v343) {}
        try {
            arrn[Material.SMOOTH_BRICK.ordinal()] = 99;
        }
        catch (NoSuchFieldError v344) {}
        try {
            arrn[Material.SMOOTH_STAIRS.ordinal()] = 110;
        }
        catch (NoSuchFieldError v345) {}
        try {
            arrn[Material.SNOW.ordinal()] = 79;
        }
        catch (NoSuchFieldError v346) {}
        try {
            arrn[Material.SNOW_BALL.ordinal()] = 290;
        }
        catch (NoSuchFieldError v347) {}
        try {
            arrn[Material.SNOW_BLOCK.ordinal()] = 81;
        }
        catch (NoSuchFieldError v348) {}
        try {
            arrn[Material.SOIL.ordinal()] = 61;
        }
        catch (NoSuchFieldError v349) {}
        try {
            arrn[Material.SOUL_SAND.ordinal()] = 89;
        }
        catch (NoSuchFieldError v350) {}
        try {
            arrn[Material.SPECKLED_MELON.ordinal()] = 340;
        }
        catch (NoSuchFieldError v351) {}
        try {
            arrn[Material.SPECTRAL_ARROW.ordinal()] = 397;
        }
        catch (NoSuchFieldError v352) {}
        try {
            arrn[Material.SPIDER_EYE.ordinal()] = 333;
        }
        catch (NoSuchFieldError v353) {}
        try {
            arrn[Material.SPLASH_POTION.ordinal()] = 396;
        }
        catch (NoSuchFieldError v354) {}
        try {
            arrn[Material.SPONGE.ordinal()] = 20;
        }
        catch (NoSuchFieldError v355) {}
        try {
            arrn[Material.SPRUCE_DOOR.ordinal()] = 194;
        }
        catch (NoSuchFieldError v356) {}
        try {
            arrn[Material.SPRUCE_DOOR_ITEM.ordinal()] = 385;
        }
        catch (NoSuchFieldError v357) {}
        try {
            arrn[Material.SPRUCE_FENCE.ordinal()] = 189;
        }
        catch (NoSuchFieldError v358) {}
        try {
            arrn[Material.SPRUCE_FENCE_GATE.ordinal()] = 184;
        }
        catch (NoSuchFieldError v359) {}
        try {
            arrn[Material.SPRUCE_WOOD_STAIRS.ordinal()] = 135;
        }
        catch (NoSuchFieldError v360) {}
        try {
            arrn[Material.STAINED_CLAY.ordinal()] = 160;
        }
        catch (NoSuchFieldError v361) {}
        try {
            arrn[Material.STAINED_GLASS.ordinal()] = 96;
        }
        catch (NoSuchFieldError v362) {}
        try {
            arrn[Material.STAINED_GLASS_PANE.ordinal()] = 161;
        }
        catch (NoSuchFieldError v363) {}
        try {
            arrn[Material.STANDING_BANNER.ordinal()] = 177;
        }
        catch (NoSuchFieldError v364) {}
        try {
            arrn[Material.STATIONARY_LAVA.ordinal()] = 12;
        }
        catch (NoSuchFieldError v365) {}
        try {
            arrn[Material.STATIONARY_WATER.ordinal()] = 10;
        }
        catch (NoSuchFieldError v366) {}
        try {
            arrn[Material.STEP.ordinal()] = 45;
        }
        catch (NoSuchFieldError v367) {}
        try {
            arrn[Material.STICK.ordinal()] = 238;
        }
        catch (NoSuchFieldError v368) {}
        try {
            arrn[Material.STONE.ordinal()] = 2;
        }
        catch (NoSuchFieldError v369) {}
        try {
            arrn[Material.STONE_AXE.ordinal()] = 233;
        }
        catch (NoSuchFieldError v370) {}
        try {
            arrn[Material.STONE_BUTTON.ordinal()] = 78;
        }
        catch (NoSuchFieldError v371) {}
        try {
            arrn[Material.STONE_HOE.ordinal()] = 249;
        }
        catch (NoSuchFieldError v372) {}
        try {
            arrn[Material.STONE_PICKAXE.ordinal()] = 232;
        }
        catch (NoSuchFieldError v373) {}
        try {
            arrn[Material.STONE_PLATE.ordinal()] = 71;
        }
        catch (NoSuchFieldError v374) {}
        try {
            arrn[Material.STONE_SLAB2.ordinal()] = 183;
        }
        catch (NoSuchFieldError v375) {}
        try {
            arrn[Material.STONE_SPADE.ordinal()] = 231;
        }
        catch (NoSuchFieldError v376) {}
        try {
            arrn[Material.STONE_SWORD.ordinal()] = 230;
        }
        catch (NoSuchFieldError v377) {}
        try {
            arrn[Material.STORAGE_MINECART.ordinal()] = 300;
        }
        catch (NoSuchFieldError v378) {}
        try {
            arrn[Material.STRING.ordinal()] = 245;
        }
        catch (NoSuchFieldError v379) {}
        try {
            arrn[Material.STRUCTURE_BLOCK.ordinal()] = 213;
        }
        catch (NoSuchFieldError v380) {}
        try {
            arrn[Material.SUGAR.ordinal()] = 311;
        }
        catch (NoSuchFieldError v381) {}
        try {
            arrn[Material.SUGAR_CANE.ordinal()] = 296;
        }
        catch (NoSuchFieldError v382) {}
        try {
            arrn[Material.SUGAR_CANE_BLOCK.ordinal()] = 84;
        }
        catch (NoSuchFieldError v383) {}
        try {
            arrn[Material.SULPHUR.ordinal()] = 247;
        }
        catch (NoSuchFieldError v384) {}
        try {
            arrn[Material.THIN_GLASS.ordinal()] = 103;
        }
        catch (NoSuchFieldError v385) {}
        try {
            arrn[Material.TIPPED_ARROW.ordinal()] = 398;
        }
        catch (NoSuchFieldError v386) {}
        try {
            arrn[Material.TNT.ordinal()] = 47;
        }
        catch (NoSuchFieldError v387) {}
        try {
            arrn[Material.TORCH.ordinal()] = 51;
        }
        catch (NoSuchFieldError v388) {}
        try {
            arrn[Material.TRAPPED_CHEST.ordinal()] = 147;
        }
        catch (NoSuchFieldError v389) {}
        try {
            arrn[Material.TRAP_DOOR.ordinal()] = 97;
        }
        catch (NoSuchFieldError v390) {}
        try {
            arrn[Material.TRIPWIRE.ordinal()] = 133;
        }
        catch (NoSuchFieldError v391) {}
        try {
            arrn[Material.TRIPWIRE_HOOK.ordinal()] = 132;
        }
        catch (NoSuchFieldError v392) {}
        try {
            arrn[Material.VINE.ordinal()] = 107;
        }
        catch (NoSuchFieldError v393) {}
        try {
            arrn[Material.WALL_BANNER.ordinal()] = 178;
        }
        catch (NoSuchFieldError v394) {}
        try {
            arrn[Material.WALL_SIGN.ordinal()] = 69;
        }
        catch (NoSuchFieldError v395) {}
        try {
            arrn[Material.WATCH.ordinal()] = 305;
        }
        catch (NoSuchFieldError v396) {}
        try {
            arrn[Material.WATER.ordinal()] = 9;
        }
        catch (NoSuchFieldError v397) {}
        try {
            arrn[Material.WATER_BUCKET.ordinal()] = 284;
        }
        catch (NoSuchFieldError v398) {}
        try {
            arrn[Material.WATER_LILY.ordinal()] = 112;
        }
        catch (NoSuchFieldError v399) {}
        try {
            arrn[Material.WEB.ordinal()] = 31;
        }
        catch (NoSuchFieldError v400) {}
        try {
            arrn[Material.WHEAT.ordinal()] = 254;
        }
        catch (NoSuchFieldError v401) {}
        try {
            arrn[Material.WOOD.ordinal()] = 6;
        }
        catch (NoSuchFieldError v402) {}
        try {
            arrn[Material.WOODEN_DOOR.ordinal()] = 65;
        }
        catch (NoSuchFieldError v403) {}
        try {
            arrn[Material.WOOD_AXE.ordinal()] = 229;
        }
        catch (NoSuchFieldError v404) {}
        try {
            arrn[Material.WOOD_BUTTON.ordinal()] = 144;
        }
        catch (NoSuchFieldError v405) {}
        try {
            arrn[Material.WOOD_DOOR.ordinal()] = 282;
        }
        catch (NoSuchFieldError v406) {}
        try {
            arrn[Material.WOOD_DOUBLE_STEP.ordinal()] = 126;
        }
        catch (NoSuchFieldError v407) {}
        try {
            arrn[Material.WOOD_HOE.ordinal()] = 248;
        }
        catch (NoSuchFieldError v408) {}
        try {
            arrn[Material.WOOD_PICKAXE.ordinal()] = 228;
        }
        catch (NoSuchFieldError v409) {}
        try {
            arrn[Material.WOOD_PLATE.ordinal()] = 73;
        }
        catch (NoSuchFieldError v410) {}
        try {
            arrn[Material.WOOD_SPADE.ordinal()] = 227;
        }
        catch (NoSuchFieldError v411) {}
        try {
            arrn[Material.WOOD_STAIRS.ordinal()] = 54;
        }
        catch (NoSuchFieldError v412) {}
        try {
            arrn[Material.WOOD_STEP.ordinal()] = 127;
        }
        catch (NoSuchFieldError v413) {}
        try {
            arrn[Material.WOOD_SWORD.ordinal()] = 226;
        }
        catch (NoSuchFieldError v414) {}
        try {
            arrn[Material.WOOL.ordinal()] = 36;
        }
        catch (NoSuchFieldError v415) {}
        try {
            arrn[Material.WORKBENCH.ordinal()] = 59;
        }
        catch (NoSuchFieldError v416) {}
        try {
            arrn[Material.WRITTEN_BOOK.ordinal()] = 345;
        }
        catch (NoSuchFieldError v417) {}
        try {
            arrn[Material.YELLOW_FLOWER.ordinal()] = 38;
        }
        catch (NoSuchFieldError v418) {}
        $SWITCH_TABLE$org$bukkit$Material = arrn;
        return $SWITCH_TABLE$org$bukkit$Material;
    }
}

