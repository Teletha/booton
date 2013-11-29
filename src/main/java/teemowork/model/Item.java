/*
 * Copyright (C) 2013 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package teemowork.model;

import static teemowork.model.Status.*;
import static teemowork.model.Version.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import js.dom.Element;

/**
 * @version 2013/06/06 20:22:51
 */
public class Item extends Describable<ItemDescriptor> {

    /** The counter for id. */
    private static int counter = 0;

    /** The item manager. */
    private static final List<Item> items = new ArrayList();

    /** Negatron Cloak */
    public static final Item NegatronCloak = new Item(1057, "Negatron Cloak");

    /** Blasting Wand */
    public static final Item BlastingWand = new Item(1026, "Blasting Wand");

    /** Abyssal Scepter */
    public static final Item Abyssal_Scepter = new Item(3001, "Abyssal Scepter", item -> {
        item.update()
                .build(BlastingWand, NegatronCloak)
                .cost(980)
                .set(AP, 70)
                .set(MR, 45)
                .abilities(Ability.AbyssalAura);
    });

    /** Aegis of the Legion */
    public static final Item AegisOftheLegion = new Item(3105, "Aegis of the Legion");

    /** Amplifying Tome */
    public static final Item AmplifyingTome = new Item(1052, "Amplifying Tome");

    /** Archangel's Staff */
    public static final Item ArchangelsStaff = new Item(3003, "Archangel's Staff");

    /** Athene's Unholy Grail */
    public static final Item AthenesUnholyGrail = new Item(3174, "Athene's Unholy Grail");

    /** Atma's Impaler */
    public static final Item AtmasImpaler = new Item(3005, "Atma's Impaler");

    /** Augment: Power */
    public static final Item AugmentPower = new Item(3196, "Augment: Power");

    /** Augment: Gravity */
    public static final Item AugmentGravity = new Item(3197, "Augment: Gravity");

    /** Augment: Death */
    public static final Item AugmentDeath = new Item(3198, "Augment: Death");

    /** Avarice Blade */
    public static final Item AvariceBlade = new Item(3093, "Avarice Blade");

    /** B. F. Sword */
    public static final Item BFSword = new Item(1038, "B. F. Sword");

    /** Banner of Command */
    public static final Item BannerOfCommand = new Item(3060, "Banner of Command");

    /** Banshee's Veil */
    public static final Item BansheesVeil = new Item(3102, "Banshee's Veil");

    /** Berserker's Greaves */
    public static final Item BerserkersGreaves = new Item(3006, "Berserker's Greaves");

    /** Bilgewater Cutlass */
    public static final Item BilgewaterCutlass = new Item(3144, "Bilgewater Cutlass");

    /** The Black Cleaver */
    public static final Item TheBlackCleaver = new Item(3071, "The Black Cleaver");

    /** Blade of the Ruined King */
    public static final Item BladeOftheRuinedKing = new Item(3153, "Blade of the Ruined King");

    /** The Bloodthirster */
    public static final Item TheBloodthirster = new Item(3072, "The Bloodthirster");

    /** Bonetooth Necklace */
    public static final Item BonetoothNecklace = new Item(3166, "Bonetooth Necklace");

    /** Boots of Mobility */
    public static final Item BootsOfMobility = new Item(3117, "Boots of Mobility");

    /** Boots of Speed */
    public static final Item BootsOfSpeed = new Item(1001, "Boots of Speed");

    /** Boots of Swiftness */
    public static final Item BootsOfSwiftness = new Item(3009, "Boots of Swiftness");

    /** Brawler's Gloves */
    public static final Item BrawlersGloves = new Item(1051, "Brawler's Gloves");

    /** The Brutalizer */
    public static final Item TheBrutalizer = new Item(3134, "The Brutalizer");

    /** Catalyst the Protector */
    public static final Item CatalystTheProtector = new Item(3010, "Catalyst the Protector");

    /** Chain Vest */
    public static final Item ChainVest = new Item(1031, "Chain Vest");

    /** Chalice of Harmony */
    public static final Item ChaliceOfHarmony = new Item(3028, "Chalice of Harmony");

    /** Cloak of Agility */
    public static final Item CloakOfAgility = new Item(1018, "Cloak of Agility");

    /** Cloth Armor */
    public static final Item ClothArmor = new Item(1029, "Cloth Armor");

    /** Crystalline Flask */
    public static final Item CrystallineFlask = new Item(2041, "Crystalline Flask");

    /** Dagger */
    public static final Item Dagger = new Item(1042, "Dagger");

    /** Deathfire Grasp */
    public static final Item DeathfireGrasp = new Item(3128, "Deathfire Grasp");

    /** Doran's Blade */
    public static final Item DoransBlade = new Item(1055, "Doran's Blade");

    /** Doran's Ring */
    public static final Item DoransRing = new Item(1056, "Doran's Ring");

    /** Doran's Shield */
    public static final Item DoransShield = new Item(1054, "Doran's Shield");

    /** Eleisa's Miracle */
    public static final Item EleisasMiracle = new Item(3173, "Eleisa's Miracle");

    /** Elixir of Brilliance */
    public static final Item ElixirOfBrilliance = new Item(2039, "Elixir of Brilliance");

    /** Elixir of Fortitude */
    public static final Item ElixirOfFortitude = new Item(2037, "Elixir of Fortitude");

    /** Emblem of Valor */
    public static final Item EmblemOfValor = new Item(3097, "Emblem of Valor");

    /** Executioner's Calling */
    public static final Item ExecutionersCalling = new Item(3123, "Executioner's Calling");

    /** Faerie Charm */
    public static final Item FaerieCharm = new Item(1004, "Faerie Charm");

    /** Fiendish Codex */
    public static final Item FiendishCodex = new Item(3108, "Fiendish Codex");

    /** Frozen Heart */
    public static final Item FrozenHeart = new Item(3110, "Frozen Heart");

    /** Frozen Mallet */
    public static final Item FrozenMallet = new Item(3022, "Frozen Mallet");

    /** Giant's Belt */
    public static final Item GiantsBelt = new Item(1011, "Giant's Belt");

    /** Glacial Shroud */
    public static final Item GlacialShroud = new Item(3024, "Glacial Shroud");

    /** Guardian Angel */
    public static final Item GuardianAngel = new Item(3026, "Guardian Angel");

    /** Guinsoo's Rageblade */
    public static final Item GuinsoosRageblade = new Item(3124, "Guinsoo's Rageblade");

    /** Haunting Guise */
    public static final Item HauntingGuise = new Item(3136, "Haunting Guise");

    /** Health Potion */
    public static final Item HealthPotion = new Item(2003, "Health Potion");

    /** The Hex Core */
    public static final Item TheHexCore = new Item(3200, "The Hex Core");

    /** Hexdrinker */
    public static final Item Hexdrinker = new Item(3155, "Hexdrinker");

    /** Hextech Gunblade */
    public static final Item HextechGunblade = new Item(3146, "Hextech Gunblade");

    /** Hextech Revolver */
    public static final Item HextechRevolver = new Item(3145, "Hextech Revolver");

    /** Hunter's Machete */
    public static final Item HuntersMachete = new Item(1039, "Hunter's Machete");

    /** Iceborn Gauntlet */
    public static final Item IcebornGauntlet = new Item(3025, "Iceborn Gauntlet");

    /** Infinity Edge */
    public static final Item InfinityEdge = new Item(3031, "Infinity Edge");

    /** Ionian Boots of Lucidity */
    public static final Item IonianBootsOfLucidity = new Item(3158, "Ionian Boots of Lucidity");

    /** Kage's Lucky Pick */
    public static final Item KagesLuckyPick = new Item(3098, "Kage's Lucky Pick");

    /** Kindlegem */
    public static final Item Kindlegem = new Item(3067, "Kindlegem");

    /** Last Whisper */
    public static final Item LastWhisper = new Item(3035, "Last Whisper");

    /** Liandry's Torment */
    public static final Item LiandrysTorment = new Item(3151, "Liandry's Torment");

    /** Lich Bane */
    public static final Item LichBane = new Item(3100, "Lich Bane");

    /** Locket of the Iron Solari */
    public static final Item LocketOftheIronSolari = new Item(3190, "Locket of the Iron Solari");

    /** Long Sword */
    public static final Item LongSword = new Item(1036, "Long Sword");

    /** Madred's Razors */
    public static final Item MadredsRazors = new Item(3106, "Madred's Razors");

    /** Malady */
    public static final Item Malady = new Item(3114, "Malady");

    /** Mana Manipulator */
    public static final Item ManaManipulator = new Item(3037, "Mana Manipulator");

    /** Mana Potion */
    public static final Item ManaPotion = new Item(2004, "Mana Potion");

    /** Manamune */
    public static final Item Manamune = new Item(3004, "Manamune");

    /** Maw of Malmortius */
    public static final Item MawOfMalmortius = new Item(3156, "Maw of Malmortius");

    /** Mejai's Soulstealer */
    public static final Item MejaisSoulstealer = new Item(3041, "Mejai's Soulstealer");

    /** Mercurial Scimitar */
    public static final Item MercurialScimitar = new Item(3139, "Mercurial Scimitar");

    /** Mercury's Treads */
    public static final Item MercurysTreads = new Item(3111, "Mercury's Treads");

    /** Mikael's Crucible */
    public static final Item MikaelsCrucible = new Item(3222, "Mikael's Crucible");

    /** Morellonomicon */
    public static final Item Morellonomicon = new Item(3165, "Morellonomicon");

    /** Muramana */
    public static final Item Muramana = new Item(3042, "Muramana");

    /** Nashor's Tooth */
    public static final Item NashorsTooth = new Item(3115, "Nashor's Tooth");

    /** Needlessly Large Rod */
    public static final Item NeedlesslyLargeRod = new Item(1058, "Needlessly Large Rod");

    /** Ninja Tabi */
    public static final Item NinjaTabi = new Item(3047, "Ninja Tabi");

    /** Null-Magic Mantle */
    public static final Item NullMagicMantle = new Item(1033, "Null-Magic Mantle");

    /** Ohmwrecker */
    public static final Item Ohmwrecker = new Item(3056, "Ohmwrecker");

    /** Oracle's Elixir */
    public static final Item OraclesElixir = new Item(2042, "Oracle's Elixir");

    /** Phage */
    public static final Item Phage = new Item(3044, "Phage");

    /** Phantom Dancer */
    public static final Item PhantomDancer = new Item(3046, "Phantom Dancer");

    /** Philosopher's Stone */
    public static final Item PhilosophersStone = new Item(3096, "Philosopher's Stone");

    /** Pickaxe */
    public static final Item Pickaxe = new Item(1037, "Pickaxe");

    /** Quicksilver Sash */
    public static final Item QuicksilverSash = new Item(3140, "Quicksilver Sash");

    /** Rabadon's Deathcap */
    public static final Item RabadonsDeathcap = new Item(3089, "Rabadon's Deathcap");

    /** Randuin's Omen */
    public static final Item RanduinsOmen = new Item(3143, "Randuin's Omen");

    /** Ravenous Hydra */
    public static final Item RavenousHydra = new Item(3074, "Ravenous Hydra");

    /** Recurve Bow */
    public static final Item RecurveBow = new Item(1043, "Recurve Bow");

    /** Rejuvenation Bead */
    public static final Item RejuvenationBead = new Item(1006, "Rejuvenation Bead");

    /** Rod of Ages */
    public static final Item RodOfAges = new Item(3027, "Rod of Ages");

    /** Ruby Crystal */
    public static final Item RubyCrystal = new Item(1028, "Ruby Crystal");

    /** Ruby Sightstone */
    public static final Item RubySightstone = new Item(2045, "Ruby Sightstone");

    /** Runaan's Hurricane */
    public static final Item RunaansHurricane = new Item(3085, "Runaan's Hurricane");

    /** Runic Bulwark */
    public static final Item RunicBulwark = new Item(3107, "Runic Bulwark");

    /** Rylai's Crystal Scepter */
    public static final Item RylaisCrystalScepter = new Item(3116, "Rylai's Crystal Scepter");

    /** Sapphire Crystal */
    public static final Item SapphireCrystal = new Item(1027, "Sapphire Crystal");

    /** Seraph's Embrace */
    public static final Item SeraphsEmbrace = new Item(3040, "Seraph's Embrace");

    /** Seeker's Armguard */
    public static final Item SeekersArmguard = new Item(3191, "Seeker's Armguard");

    /** Shard of True Ice */
    public static final Item ShardOfTrueIce = new Item(3092, "Shard of True Ice");

    /** Sheen */
    public static final Item Sheen = new Item(3057, "Sheen");

    /** Shurelya's Reverie */
    public static final Item ShurelyasReverie = new Item(3069, "Shurelya's Reverie");

    /** Sight Ward */
    public static final Item StealthWard = new Item(2044, "Stealth Ward");

    /** Sightstone */
    public static final Item Sightstone = new Item(2049, "Sightstone");

    /** Sorcerer's Shoes */
    public static final Item SorcerersShoes = new Item(3020, "Sorcerer's Shoes");

    /** Spectre's Cowl */
    public static final Item SpectresCowl = new Item(9999, "Spectre's Cowl");

    /** Spirit Stone */
    public static final Item SpiritStone = new Item(1080, "Spirit Stone");

    /** Spirit of the Ancient Golem */
    public static final Item SpiritOftheAncientGolem = new Item(3207, "Spirit of the Ancient Golem");

    /** Spirit of the Elder Lizard */
    public static final Item SpiritOftheElderLizard = new Item(3209, "Spirit of the Elder Lizard");

    /** Spirit of the Spectral Wraith */
    public static final Item SpiritOftheSpectralWraith = new Item(3206, "Spirit of the Spectral Wraith");

    /** Spirit Visage */
    public static final Item SpiritVisage = new Item(3065, "Spirit Visage");

    /** Statikk Shiv */
    public static final Item StatikkShiv = new Item(3087, "Statikk Shiv");

    /** Stinger */
    public static final Item Stinger = new Item(3101, "Stinger");

    /** Sunfire Cape */
    public static final Item SunfireCape = new Item(3068, "Sunfire Cape");

    /** Sword of the Divine */
    public static final Item SwordOftheDivine = new Item(3131, "Sword of the Divine");

    /** Sword of the Occult */
    public static final Item SwordOftheOccult = new Item(3141, "Sword of the Occult");

    /** Tear of the Goddess */
    public static final Item TearOftheGoddess = new Item(3070, "Tear of the Goddess");

    /** Thornmail */
    public static final Item Thornmail = new Item(3075, "Thornmail");

    /** Tiamat */
    public static final Item Tiamat = new Item(3077, "Tiamat");

    /** Trinity Force */
    public static final Item TrinityForce = new Item(3078, "Trinity Force");

    /** Twin Shadows */
    public static final Item TwinShadows = new Item(3023, "Twin Shadows");

    /** Vampiric Scepter */
    public static final Item VampiricScepter = new Item(1053, "Vampiric Scepter");

    /** Vision Ward */
    public static final Item VisionWard = new Item(2043, "Vision Ward");

    /** Void Staff */
    public static final Item VoidStaff = new Item(3135, "Void Staff");

    /** Warden's Mail */
    public static final Item WardensMail = new Item(3082, "Warden's Mail");

    /** Warmog's Armor */
    public static final Item WarmogsArmor = new Item(3083, "Warmog's Armor");

    /** Will of the Ancients */
    public static final Item WillOftheAncients = new Item(3152, "Will of the Ancients");

    /** Wit's End */
    public static final Item WitsEnd = new Item(3091, "Wit's End");

    /** Wriggle's Lantern */
    public static final Item WrigglesLantern = new Item(3154, "Wriggle's Lantern");

    /** Youmuu's Ghostblade */
    public static final Item YoumuusGhostblade = new Item(3142, "Youmuu's Ghostblade");

    /** Zeal */
    public static final Item Zeal = new Item(3086, "Zeal");

    /** Zeke's Herald */
    public static final Item ZekesHerald = new Item(3050, "Zeke's Herald");

    /** Zephyr */
    public static final Item Zephyr = new Item(3172, "Zephyr");

    /** Zhonya's Hourglass */
    public static final Item ZhonyasHourglass = new Item(3157, "Zhonya's Hourglass");

    /** Spellthief's Edge */
    public static final Item SpellthiefsEdge = new Item(3303, "Spellthief's Edge");

    /** The sequencial id. */
    public final int position;

    /** The item id. */
    public final int id;

    /** The item name. */
    public final String name;

    private Consumer<Item> descriptor;

    /**
     * <p>
     * Create constructor.
     * </p>
     * 
     * @param id
     * @param name
     */
    Item(int id, String name) {
        this.position = counter++;
        this.id = id;
        this.name = name;

        items.add(this);
    }

    /**
     * <p>
     * Create constructor.
     * </p>
     * 
     * @param id
     * @param name
     */
    Item(int id, String name, Consumer<Item> descriptor) {
        this.position = counter++;
        this.id = id;
        this.name = name;
        this.descriptor = descriptor;

        items.add(this);
    }

    /**
     * <p>
     * Apply icon image.
     * </p>
     */
    public void applyIcon(Element element) {
        element.css("background-image", "url(src/main/resources/teemowork/items.jpg)")
                .css("background-position", position / (counter - 1) * 100 + "% 0%")
                .css("background-size", "cover")
                .css("background-origin", "border-box");
    }

    public String getIcon() {
        return "src/main/resources/teemowork/items.jpg";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxLevel() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ItemDescriptor createDescriptor(ItemDescriptor previous) {
        return new ItemDescriptor(this, previous);
    }

    /**
     * <p>
     * Calcurate item total cost.
     * </p>
     * 
     * @param version
     * @return
     */
    public double getTotalCost(Version version) {
        ItemDescriptor status = getDescriptor(version);

        double sum = status.get(Cost);

        for (Item material : status.getBuild()) {
            sum += material.getTotalCost(version);
        }
        return sum;
    }

    /**
     * <p>
     * List up all Items.
     * </p>
     * 
     * @return
     */
    public static List<Item> getAll() {
        return items;
    }

    /**
     * <p>
     * Find Item by name.
     * </p>
     * 
     * @param name A Item name.
     * @return A matched Item.
     */
    public static Item getByName(String name) {
        for (Item item : items) {
            if (item.name.equals(name)) {
                return item;
            }
        }
        return null;
    }

    static {
        AegisOftheLegion.update()
                .build(EmblemOfValor, NullMagicMantle, RubyCrystal)
                .cost(625)
                .set(Health, 250)
                .set(AR, 20)
                .set(MR, 20)
                .abilities(Ability.AegisLegion);
        AegisOftheLegion.update(P310).cost(375).set(Health, 200).set(MR, 0);
        AegisOftheLegion.update(P314).build(RejuvenationBead, ClothArmor, RubyCrystal, NullMagicMantle).cost(595);

        AmplifyingTome.update().cost(435).set(AP, 20);
        ArchangelsStaff.update()
                .build(TearOftheGoddess, BlastingWand)
                .cost(1140)
                .set(AP, 60)
                .set(Mreg, 10)
                .set(Mana, 250)
                .abilities(Ability.ArchangelInsight, Ability.ManaCharge);
        AthenesUnholyGrail.update()
                .build(ChaliceOfHarmony, FiendishCodex)
                .cost(900)
                .set(AP, 60)
                .set(Mreg, 15)
                .set(MR, 40)
                .set(CDR, 20)
                .abilities(Ability.AtheneRestore, Ability.ManaFont);
        AtmasImpaler.update()
                .build(AvariceBlade, ChainVest)
                .cost(780)
                .set(AR, 45)
                .set(Critical, 15)
                .abilities(Ability.AtamDamage);
        AugmentDeath.update()
                .build(TheHexCore)
                .cost(1000)
                .set(AP, 45)
                .abilities(Ability.HexCorePower, Ability.HexCoreDeath);
        AugmentGravity.update()
                .build(TheHexCore)
                .cost(1000)
                .set(CDR, 10)
                .set(Mreg, 5)
                .set(Mana, 200)
                .abilities(Ability.HexCorePower, Ability.HexCoreGravity);
        AugmentPower.update()
                .build(TheHexCore)
                .cost(1000)
                .set(Health, 220)
                .set(Hreg, 6)
                .abilities(Ability.HexCorePower, Ability.HexCoreTransfer);
        AvariceBlade.update()
                .build(BrawlersGloves)
                .cost(400)
                .set(Critical, 10)
                .abilities(Ability.Avarice, Ability.Greed);
        BFSword.update().cost(1550).set(AD, 45);

        BannerOfCommand.update()
                .build(FiendishCodex, EmblemOfValor)
                .cost(890)
                .set(AP, 40)
                .set(CDR, 10)
                .set(AR, 30)
                .abilities(Ability.AegisValor, Ability.Promote);
        BannerOfCommand.update(P314).build(FiendishCodex, BlastingWand).cost(720).set(AP, 80).set(CDR, 20).set(AR, 0);

        BansheesVeil.update()
                .build(NegatronCloak, CatalystTheProtector)
                .cost(600)
                .set(Health, 400)
                .set(Mana, 300)
                .set(MR, 45)
                .abilities(Ability.BansheeShield);
        BansheesVeil.update(P310)
                .build(SpectresCowl, RubyCrystal)
                .cost(875)
                .set(Health, 450)
                .set(MR, 55)
                .set(Mana, 0)
                .abilities(Ability.BansheeShield, Ability.BansheeRegene);

        BerserkersGreaves.update()
                .build(BootsOfSpeed, Dagger)
                .cost(150)
                .set(ASRatio, 20)
                .abilities(Ability.EnhancedMovement2);
        BerserkersGreaves.update(P308).cost(175);

        BilgewaterCutlass.update()
                .build(LongSword, VampiricScepter)
                .cost(150)
                .set(LS, 12)
                .set(AD, 25)
                .abilities(Ability.Bilgewater);
        BladeOftheRuinedKing.update()
                .build(BilgewaterCutlass, Dagger, Dagger)
                .cost(1000)
                .set(LS, 15)
                .set(AD, 25)
                .set(ASRatio, 40)
                .abilities(Ability.BotRKPassive, Ability.BotRKActive);
        BlastingWand.update().cost(860).set(AP, 40);

        BonetoothNecklace.update()
                .cost(800)
                .set(AD, 5)
                .abilities(Ability.BonetoothNecklaceDamage, Ability.BonetoothNecklaceSpecial);

        BootsOfMobility.update().build(BootsOfSpeed).cost(650).abilities(Ability.EnhancedMovement5);
        BootsOfMobility.update(P308).cost(675);

        BootsOfSpeed.update().cost(350).abilities(Ability.EnhancedMovement1);
        BootsOfSpeed.update(P308).cost(325);

        BootsOfSwiftness.update()
                .build(BootsOfSpeed)
                .cost(650)
                .abilities(Ability.EnhancedMovement3, Ability.SlowResist);
        BootsOfSwiftness.update(P308).cost(675);

        BrawlersGloves.update().cost(400).set(Critical, 8);
        CatalystTheProtector.update()
                .build(RubyCrystal, SapphireCrystal)
                .cost(325)
                .set(Health, 200)
                .set(Mana, 300)
                .abilities(Ability.ValorsReward);
        ChainVest.update().cost(720).set(AR, 40);
        ChaliceOfHarmony.update()
                .build(FaerieCharm, FaerieCharm, NullMagicMantle)
                .cost(120)
                .set(Mreg, 7)
                .set(MR, 25)
                .abilities(Ability.ManaFont);
        CloakOfAgility.update().cost(730).set(Critical, 15);
        ClothArmor.update().cost(300).set(AR, 15);
        CrystallineFlask.update().cost(345).abilities(Ability.CrystallineFlaskActive, Ability.CrystallineFlaskCharge);
        Dagger.update().cost(400).set(ASRatio, 12);
        DeathfireGrasp.update()
                .build(FiendishCodex, NeedlesslyLargeRod)
                .cost(680)
                .set(AP, 120)
                .set(CDR, 10)
                .abilities(Ability.DeathfireGraspActive);
        DoransBlade.update().cost(475).set(AD, 10).set(Health, 80).abilities(Ability.DransBladePassive);
        DoransBlade.update(P314).cost(440).set(AD, 8);

        DoransRing.update().cost(475).set(AP, 15).set(Health, 80).set(Mreg, 3).abilities(Ability.DransRingPassive);
        DoransRing.update(P308).cost(400).set(Health, 60);

        DoransShield.update().cost(475).set(Health, 100).set(Hreg, 8).set(AR, 5).abilities(Ability.DransShieldPassive);
        DoransShield.update(P308).cost(440).set(AR, 0).set(Hreg, 10);

        EleisasMiracle.update()
                .build(PhilosophersStone)
                .cost(400)
                .set(Mreg, 15)
                .set(Hreg, 10)
                .abilities(Ability.Aid, Ability.EleisasBlessing);
        EleisasMiracle.update(P314).deprecated();

        ElixirOfBrilliance.update().cost(250).abilities(Ability.ElixirOfBrillianceActive);
        ElixirOfFortitude.update().cost(350).abilities(Ability.ElixirOfFortitudeActive);
        EmblemOfValor.update().build(ClothArmor, RejuvenationBead).cost(170).set(AR, 20).abilities(Ability.Valor);
        ExecutionersCalling.update()
                .build(AvariceBlade, LongSword)
                .cost(700)
                .set(AD, 25)
                .set(Critical, 20)
                .abilities(Ability.ExecutionersCallingPassive);
        FaerieCharm.update().cost(180).set(Mreg, 3);
        FiendishCodex.update().build(AmplifyingTome).cost(385).set(AP, 30).abilities(Ability.FiendishCodexPassive);
        FrozenHeart.update()
                .build(GlacialShroud, WardensMail)
                .cost(550)
                .set(CDR, 20)
                .set(AR, 95)
                .set(Mana, 400)
                .abilities(Ability.FrozenHeartPassive);
        FrozenMallet.update().build(Phage, GiantsBelt).cost(835).set(AD, 30).set(Health, 700).abilities(Ability.Icy2);
        FrozenMallet.update(P310A).build(GiantsBelt, Pickaxe, RubyCrystal).cost(950);

        GiantsBelt.update().cost(1000).set(Health, 380);
        GlacialShroud.update()
                .build(SapphireCrystal, ChainVest)
                .cost(230)
                .set(AR, 45)
                .set(Mana, 300)
                .abilities(Ability.GlacialShroudPassive);
        GuardianAngel.update()
                .build(NullMagicMantle, ChainVest)
                .cost(1480)
                .set(AR, 50)
                .set(MR, 30)
                .abilities(Ability.GuardianAngelPassive);
        GuardianAngel.update(P310).build(NegatronCloak, ChainVest).cost(1310).set(MR, 40);
        GuinsoosRageblade.update()
                .build(BlastingWand, Pickaxe)
                .cost(865)
                .set(AD, 30)
                .set(AP, 40)
                .abilities(Ability.GuinsooPassive, Ability.GuinsooUniquePassive);
        HauntingGuise.update()
                .build(RubyCrystal, AmplifyingTome)
                .cost(575)
                .set(AP, 25)
                .set(Health, 200)
                .abilities(Ability.EyesOfPain);
        HealthPotion.update().cost(35).abilities(Ability.PortionHealth);
        Hexdrinker.update()
                .build(LongSword, NullMagicMantle)
                .cost(550)
                .set(AD, 25)
                .set(MR, 25)
                .abilities(Ability.Lifeline1);
        HextechGunblade.update()
                .build(BilgewaterCutlass, HextechRevolver)
                .cost(800)
                .set(LS, 12)
                .set(AD, 45)
                .set(AP, 65)
                .abilities(Ability.GunbladePassive, Ability.Reload, Ability.GunbladeActive);
        HextechRevolver.update()
                .build(AmplifyingTome, AmplifyingTome)
                .cost(330)
                .set(AP, 40)
                .abilities(Ability.HextechRevolverPassive);

        HuntersMachete.update().cost(300).abilities(Ability.Butcher1, Ability.Rend);
        HuntersMachete.update(P308).abilities(Ability.Butcher1, Ability.Maim1);

        IcebornGauntlet.update()
                .build(Sheen, GlacialShroud)
                .cost(700)
                .set(AP, 30)
                .set(CDR, 10)
                .set(AR, 70)
                .set(Mana, 500)
                .abilities(Ability.Spellblade);
        InfinityEdge.update()
                .build(BFSword, CloakOfAgility, Pickaxe)
                .cost(645)
                .set(AD, 70)
                .set(Critical, 25)
                .abilities(Ability.InfinityEdgePassive);

        IonianBootsOfLucidity.update()
                .build(BootsOfSpeed)
                .cost(700)
                .abilities(Ability.IonianCDR, Ability.EnhancedMovement2);
        IonianBootsOfLucidity.update(P306).cost(650);
        IonianBootsOfLucidity.update(P308).cost(675);

        KagesLuckyPick.update().build(AmplifyingTome).cost(330).set(AP, 25).abilities(Ability.LuckyShadow);
        Kindlegem.update().build(RubyCrystal).cost(375).set(Health, 200).abilities(Ability.KindlegemPassive);
        LastWhisper.update().build(LongSword, Pickaxe).cost(1025).set(AD, 40).abilities(Ability.LastWhisperPassive);
        LiandrysTorment.update()
                .build(HauntingGuise, AmplifyingTome)
                .cost(980)
                .set(AP, 50)
                .set(Health, 300)
                .abilities(Ability.EyesOfPain, Ability.LiandrysTormentPassive);
        LichBane.update()
                .build(Sheen, BlastingWand)
                .cost(940)
                .set(AP, 80)
                .set(Mana, 250)
                .set(MSRatio, 5)
                .abilities(Ability.LichSpellblade);
        LocketOftheIronSolari.update()
                .build(Kindlegem, ClothArmor, RejuvenationBead)
                .cost(520)
                .set(Health, 300)
                .set(CDR, 10)
                .set(AR, 35)
                .set(Hreg, 5)
                .abilities(Ability.SolariActive);
        LocketOftheIronSolari.update(P310)
                .build(AegisOftheLegion)
                .cost(600)
                .set(Health, 300)
                .set(AR, 20)
                .set(CDR, 10)
                .set(Hreg, 0)
                .abilities(Ability.SolariActive, Ability.AegisLegion);

        LongSword.update().cost(400).set(AD, 10);
        LongSword.update(P314).cost(360);

        MadredsRazors.update()
                .build(ClothArmor, HuntersMachete)
                .cost(100)
                .set(AR, 25)
                .abilities(Ability.Maim1, Ability.Rend);
        MadredsRazors.update(P308).abilities(Ability.Maim2);

        Malady.update()
                .build(Dagger, Dagger, AmplifyingTome)
                .cost(800)
                .set(AP, 25)
                .set(ASRatio, 45)
                .abilities(Ability.MaladyPassive);
        Malady.update(P308).deprecated();

        ManaManipulator.update().build(FaerieCharm, FaerieCharm).cost(40).abilities(Ability.ManaWarp);
        ManaManipulator.update(P307).build(FaerieCharm).cost(120).abilities(Ability.ManaWarp);
        ManaManipulator.update(P314).deprecated();

        Manamune.update()
                .build(TearOftheGoddess, LongSword)
                .cost(1000)
                .set(AD, 20)
                .set(Mreg, 7)
                .set(Mana, 250)
                .abilities(Ability.Awe, Ability.ManamuneManaCharge);
        ManaPotion.update().cost(35).abilities(Ability.PortionMana);
        MawOfMalmortius.update()
                .build(Hexdrinker, Pickaxe)
                .cost(975)
                .set(AD, 55)
                .set(MR, 36)
                .abilities(Ability.MawOfMalmortiusPassive, Ability.Lifeline2);
        MawOfMalmortius.update(P309).set(AD, 60).set(MR, 40);
        MejaisSoulstealer.update()
                .build(AmplifyingTome)
                .cost(800)
                .set(AP, 20)
                .abilities(Ability.MejaisSoulstealerPassive);
        MercurialScimitar.update()
                .build(QuicksilverSash, BFSword)
                .cost(600)
                .set(AD, 60)
                .set(MR, 45)
                .abilities(Ability.Quicksilver2);
        MercurysTreads.update()
                .build(BootsOfSpeed, NullMagicMantle)
                .cost(450)
                .set(MR, 25)
                .abilities(Ability.EnhancedMovement2, Ability.TenacityPassive);
        MercurysTreads.update(P308).cost(475);

        MikaelsCrucible.update()
                .build(ChaliceOfHarmony, SapphireCrystal)
                .cost(920)
                .set(Mreg, 9)
                .set(Mana, 300)
                .set(MR, 40)
                .abilities(Ability.ManaFont, Ability.MikaelsCrucibleActive);
        MikaelsCrucible.update(P307)
                .build(ChaliceOfHarmony, PhilosophersStone)
                .cost(920)
                .set(Mreg, 18)
                .set(Hreg, 7)
                .set(MR, 40)
                .abilities(Ability.ManaFont, Ability.MikaelsCrucibleActive);
        MikaelsCrucible.update(P314).build(ChaliceOfHarmony).cost(720).set(Mreg, 12).set(Hreg, 0);

        Morellonomicon.update()
                .build(FaerieCharm, FiendishCodex, KagesLuckyPick)
                .cost(435)
                .set(AP, 75)
                .set(CDR, 20)
                .set(Mreg, 12)
                .abilities(Ability.MorellonomiconPassive);
        Muramana.update()
                .build(Manamune)
                .cost(0)
                .set(AD, 20)
                .set(Mreg, 7)
                .set(Mana, 1000)
                .abilities(Ability.Awe, Ability.MuramanaToggle);
        NashorsTooth.update()
                .build(Stinger, FiendishCodex)
                .cost(430)
                .set(AP, 65)
                .set(ASRatio, 50)
                .abilities(Ability.NashorsToothPassive);
        NashorsTooth.update(P308).cost(920).set(AP, 60);

        NeedlesslyLargeRod.update().cost(1600).set(AP, 80);
        NegatronCloak.update().cost(720).set(MR, 40);

        NinjaTabi.update()
                .build(BootsOfSpeed, ClothArmor)
                .cost(350)
                .set(AR, 25)
                .abilities(Ability.NinjaTabiPassive, Ability.EnhancedMovement2);
        NinjaTabi.update(P308).cost(375);

        NullMagicMantle.update().cost(400).set(MR, 20);
        Ohmwrecker.update()
                .build(RubyCrystal, BlastingWand, PhilosophersStone)
                .cost(800)
                .set(AP, 50)
                .set(Health, 350)
                .set(Mreg, 15)
                .set(Hreg, 15)
                .abilities(Ability.OhmwreckerActive);
        Ohmwrecker.update(P314)
                .build(RubyCrystal, BlastingWand)
                .cost(665)
                .set(AP, 50)
                .set(Health, 350)
                .set(Mreg, 0)
                .set(Hreg, 0)
                .abilities(Ability.OhmwreckerActive);

        OraclesElixir.update().cost(400).abilities(Ability.OraclesElixirActive);
        OraclesElixir.update(P314).deprecated();

        Phage.update().build(RubyCrystal, LongSword).cost(590).set(AD, 20).set(Health, 200).abilities(Ability.Icy1);
        Phage.update(P310A).cost(375).abilities(Ability.Rage);
        Phage.update(P312).cost(475);

        PhantomDancer.update()
                .build(CloakOfAgility, Zeal, Dagger)
                .cost(495)
                .set(ASRatio, 50)
                .set(MSRatio, 5)
                .set(Critical, 30)
                .abilities(Ability.PhantomDancerPassive);
        PhilosophersStone.update()
                .build(FaerieCharm, RejuvenationBead)
                .cost(340)
                .set(Mreg, 9)
                .set(Hreg, 7)
                .abilities(Ability.Transmute);
        Pickaxe.update().cost(875).set(AD, 25);
        QuicksilverSash.update().build(NegatronCloak).cost(830).set(MR, 45).abilities(Ability.Quicksilver1);
        RabadonsDeathcap.update()
                .build(BlastingWand, NeedlesslyLargeRod)
                .cost(840)
                .set(AP, 120)
                .abilities(Ability.RabadonsDeathcapPassive);
        RanduinsOmen.update()
                .build(GiantsBelt, WardensMail)
                .cost(1000)
                .set(Health, 500)
                .set(AR, 70)
                .abilities(Ability.ColdSteel2, Ability.RanduinsOmenAvtive);

        RavenousHydra.update()
                .build(Tiamat, VampiricScepter)
                .cost(200)
                .set(LS, 12)
                .set(AD, 75)
                .set(Hreg, 15)
                .abilities(Ability.RavenousHydraPassive, Ability.Cleave, Ability.Crescent);
        RavenousHydra.update(P306).cost(600);

        RecurveBow.update().cost(950).set(ASRatio, 30);
        RecurveBow.update(P308).cost(900);

        RejuvenationBead.update().cost(180).set(Hreg, 5);
        RodOfAges.update()
                .build(CatalystTheProtector, BlastingWand)
                .cost(740)
                .set(AP, 60)
                .set(Health, 450)
                .set(Mana, 450)
                .abilities(Ability.RodOfAgesPassive, Ability.ValorsReward);
        RubyCrystal.update().cost(475).set(Health, 180);

        RubySightstone.update()
                .build(RubyCrystal, Sightstone)
                .cost(125)
                .set(Health, 360)
                .abilities(Ability.WardRefresh2, Ability.GhostWard2);
        RubySightstone.update(P314).abilities(Ability.WardRefresh2, Ability.GhostWard1);

        RunaansHurricane.update()
                .build(Dagger, RecurveBow, Dagger)
                .cost(700)
                .set(ASRatio, 70)
                .abilities(Ability.RunaansHurricanePassive);
        RunicBulwark.update()
                .build(NullMagicMantle, AegisOftheLegion)
                .cost(400)
                .set(Health, 300)
                .set(AR, 20)
                .set(MR, 30)
                .abilities(Ability.BulwarkLegion);
        RunicBulwark.update(P310).deprecated();
        RylaisCrystalScepter.update()
                .build(BlastingWand, AmplifyingTome, GiantsBelt)
                .cost(605)
                .set(AP, 80)
                .set(Health, 500)
                .abilities(Ability.RylaisCrystalScepterPassive);
        SapphireCrystal.update().cost(400).set(Mana, 200);
        SeekersArmguard.update()
                .build(AmplifyingTome, ClothArmor, ClothArmor)
                .cost(125)
                .set(AP, 20)
                .set(AR, 30)
                .abilities(Ability.SeekersArmguardPassive);
        SeraphsEmbrace.update()
                .build(ArchangelsStaff)
                .cost(0)
                .set(AP, 60)
                .set(Mreg, 10)
                .set(Mana, 1000)
                .abilities(Ability.Insight, Ability.ManaShield);

        ShardOfTrueIce.update()
                .build(KagesLuckyPick, ManaManipulator)
                .cost(535)
                .set(AP, 45)
                .abilities(Ability.LuckyShadow, Ability.ManaWarp, Ability.ShardOfTrueIceActive);

        Sheen.update()
                .build(SapphireCrystal, AmplifyingTome)
                .cost(365)
                .set(AP, 25)
                .set(Mana, 200)
                .abilities(Ability.SheenSpellblade);
        ShurelyasReverie.update()
                .build(Kindlegem, PhilosophersStone)
                .cost(550)
                .set(Health, 250)
                .set(CDR, 10)
                .set(Mreg, 10)
                .set(Hreg, 10)
                .abilities(Ability.ShurelyasReverieAvtive);
        Sightstone.update()
                .build(RubyCrystal)
                .cost(475)
                .set(Health, 180)
                .abilities(Ability.WardRefresh1, Ability.GhostWard1);
        StealthWard.update().cost(75).abilities(Ability.StealthWardAvtive);

        SorcerersShoes.update().build(BootsOfSpeed).cost(750).set(MRPen, 15).abilities(Ability.EnhancedMovement2);
        SorcerersShoes.update(P308).cost(775);

        SpectresCowl.update()
                .build(NegatronCloak, RubyCrystal)
                .cost(205)
                .set(Health, 200)
                .set(MR, 45)
                .abilities(Ability.SpectresCowlPassive);

        SpiritOftheAncientGolem.update()
                .build(SpiritStone, Kindlegem)
                .cost(450)
                .set(Health, 500)
                .set(Mreg, 7)
                .set(Hreg, 14)
                .set(CDR, 10)
                .abilities(Ability.Butcher3, Ability.TenacityPassive);

        SpiritOftheElderLizard.update()
                .build(SpiritStone, LongSword, LongSword)
                .cost(500)
                .set(AD, 45)
                .set(CDR, 10)
                .set(Mreg, 7)
                .set(Hreg, 14)
                .abilities(Ability.Butcher3, Ability.Incinerate);
        SpiritOftheElderLizard.update(P308).set(AD, 35);

        SpiritOftheSpectralWraith.update()
                .build(SpiritStone, HextechRevolver)
                .cost(100)
                .set(AP, 50)
                .set(CDR, 10)
                .set(Mreg, 10)
                .abilities(Ability.SpiritOftheSpectralWraithSV, Ability.Butcher3, Ability.SpiritOftheSpectralWraithSmite);
        SpiritOftheSpectralWraith.update(P308).set(AP, 40);

        SpiritStone.update()
                .build(HuntersMachete, FaerieCharm, RejuvenationBead)
                .cost(40)
                .set(Mreg, 7)
                .set(Hreg, 14)
                .abilities(Ability.Butcher2, Ability.Rend);
        SpiritStone.update(P308).abilities(Ability.Butcher2, Ability.Maim1);

        SpiritVisage.update()
                .build(Kindlegem, NegatronCloak)
                .cost(630)
                .set(Health, 200)
                .set(CDR, 20)
                .set(MR, 50)
                .abilities(Ability.SpiritVisagePassive);
        SpiritVisage.update(P310).build(SpectresCowl, Kindlegem).cost(375).set(Health, 400).set(MR, 55).set(Hreg, 20);
        SpiritVisage.update(P310A).cost(500);
        StatikkShiv.update()
                .build(Zeal, AvariceBlade)
                .cost(525)
                .set(ASRatio, 40)
                .set(MSRatio, 6)
                .set(Critical, 20)
                .abilities(Ability.StatikkShivPassive);
        Stinger.update().build(Dagger, Dagger).cost(450).set(ASRatio, 40).abilities(Ability.StingerPassive);
        SunfireCape.update()
                .build(ChainVest, GiantsBelt)
                .cost(930)
                .set(Health, 450)
                .set(AR, 45)
                .abilities(Ability.SunfireCapePassive);
        SwordOftheDivine.update()
                .build(RecurveBow, Dagger)
                .cost(850)
                .set(ASRatio, 45)
                .abilities(Ability.SwordOftheDivinePassive, Ability.SwordOftheDivineActive);
        SwordOftheOccult.update().build(LongSword).cost(800).set(AD, 10).abilities(Ability.SwordOftheOccultPassive);
        TearOftheGoddess.update()
                .build(SapphireCrystal, FaerieCharm)
                .cost(120)
                .set(Mreg, 7)
                .set(Mana, 250)
                .abilities(Ability.TearManaCharge);
        TheBlackCleaver.update()
                .build(TheBrutalizer, RubyCrystal)
                .cost(1188)
                .set(AD, 50)
                .set(Health, 200)
                .set(CDR, 10)
                .abilities(Ability.TheBlackCleaverPassive, Ability.TheBlackCleaverUniquePassive);
        TheBloodthirster.update()
                .build(BFSword, VampiricScepter)
                .cost(850)
                .set(LS, 12)
                .set(AD, 70)
                .abilities(Ability.TheBloodthirsterPassive);
        TheBrutalizer.update()
                .build(LongSword, LongSword)
                .cost(537)
                .set(AD, 25)
                .abilities(Ability.TheBrutalizerPassive);
        TheHexCore.update().cost(0).abilities(Ability.TheHexCorePassive);
        Thornmail.update().build(ChainVest, ClothArmor).cost(1180).set(AR, 100).abilities(Ability.ThornmailPassive);

        Tiamat.update()
                .build(Pickaxe, LongSword, RejuvenationBead, RejuvenationBead)
                .cost(665)
                .set(AD, 50)
                .set(Hreg, 15)
                .abilities(Ability.Cleave, Ability.Crescent);
        Tiamat.update(P306).cost(265).set(AD, 40);

        TrinityForce.update()
                .build(Zeal, Sheen, Phage)
                .cost(3)
                .set(AD, 30)
                .set(AP, 30)
                .set(Health, 250)
                .set(ASRatio, 30)
                .set(Mana, 200)
                .set(MSRatio, 8)
                .set(Critical, 10)
                .abilities(Ability.Icy1, Ability.TrinitySpellblade);
        TrinityForce.update(P310A).abilities(Ability.Rage, Ability.TrinitySpellblade);

        TwinShadows.update()
                .build(KagesLuckyPick, NullMagicMantle)
                .cost(735)
                .set(AP, 40)
                .set(MSRatio, 6)
                .set(MR, 40)
                .abilities(Ability.Hunt);
        TwinShadows.update(P314).build(AmplifyingTome, AmplifyingTome, NullMagicMantle).cost(730).set(AP, 50);

        VampiricScepter.update().build(LongSword).cost(400).set(LS, 10).set(AD, 10);
        VisionWard.update().cost(125).abilities(Ability.VisionWardAvtive);
        VisionWard.update(P314).cost(100);

        VoidStaff.update()
                .build(BlastingWand, AmplifyingTome)
                .cost(1000)
                .set(AP, 70)
                .abilities(Ability.VoidStaffPassive);

        WardensMail.update().build(ClothArmor, ClothArmor).cost(500).set(AR, 50).abilities(Ability.ColdSteel1);
        WardensMail.update(P310).cost(400);

        WarmogsArmor.update()
                .build(GiantsBelt, RubyCrystal, RejuvenationBead, RejuvenationBead)
                .cost(995)
                .set(Health, 1000)
                .abilities(Ability.WarmogsArmorPassive);

        WillOftheAncients.update()
                .build(KagesLuckyPick, HextechRevolver)
                .cost(585)
                .set(AP, 50)
                .abilities(Ability.WillOftheAncientsAura);
        WillOftheAncients.update(P314)
                .build(HextechRevolver, FaerieCharm, FaerieCharm)
                .cost(440)
                .set(AP, 50)
                .set(Mreg, 10)
                .set(CDR, 10)
                .set(SV, 20)
                .abilities();

        WitsEnd.update()
                .build(RecurveBow, NullMagicMantle)
                .cost(850)
                .set(ASRatio, 40)
                .set(MR, 25)
                .abilities(Ability.WitsEndPassive);
        WitsEnd.update(P308).build(RecurveBow, NullMagicMantle, Dagger).cost(700).set(ASRatio, 42);

        WrigglesLantern.update()
                .build(VampiricScepter, MadredsRazors)
                .cost(100)
                .set(LS, 10)
                .set(AD, 15)
                .set(AR, 30)
                .abilities(Ability.Maim2, Ability.WrigglesLanternAvtive);
        WrigglesLantern.update(P308)
                .set(AD, 25)
                .set(LS, 15)
                .set(AR, 25)
                .abilities(Ability.Maim3, Ability.WrigglesLanternAvtive)
                .cost(500);

        YoumuusGhostblade.update()
                .build(AvariceBlade, TheBrutalizer)
                .cost(563)
                .set(AD, 30)
                .set(CDR, 10)
                .set(Critical, 15)
                .abilities(Ability.YoumuusGhostbladePassive, Ability.YoumuusGhostbladeActive);
        Zeal.update().build(BrawlersGloves, Dagger).cost(375).set(ASRatio, 18).set(MSRatio, 5).set(Critical, 10);
        ZekesHerald.update()
                .build(VampiricScepter, Kindlegem)
                .cost(900)
                .set(Health, 250)
                .set(CDR, 20)
                .abilities(Ability.ZekesHeraldAura);
        Zephyr.update()
                .build(Stinger, Pickaxe)
                .cost(725)
                .set(AD, 25)
                .set(CDR, 10)
                .set(ASRatio, 50)
                .set(MSRatio, 10)
                .abilities(Ability.TenacityPassive);
        ZhonyasHourglass.update()
                .build(NeedlesslyLargeRod, SeekersArmguard)
                .cost(500)
                .set(AP, 120)
                .set(AR, 50)
                .abilities(Ability.Stasis);

        try {
            for (Field field : Item.class.getFields()) {
                if (Modifier.isStatic(field.getModifiers())) {
                    System.out.println(field.getName());
                    Item item = (Item) field.get(null);

                    if (item.descriptor != null) {
                        item.update();
                        item.descriptor.accept(item);
                    }
                }
            }
        } catch (Exception e) {
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error(e);
        }
    }
}
