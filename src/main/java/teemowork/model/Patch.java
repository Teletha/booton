/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package teemowork.model;

import java.util.Map;

import js.util.HashMap;

/**
 * @version 2012/12/06 23:07:37
 */
public class Patch {

    /** The update number. */
    public final int number;

    /** The update name. */
    public final String name;

    /** The previous {@link Patch}. */
    private final Patch previous;

    /** The champion list. */
    private final Map<String, Champion> champions = new HashMap();

    /** The item list. */
    private final Map<String, Item> items = new HashMap();

    /**
     * Create patch information.
     */
    Patch(int number, int year, int month, int day, String name, Patch previous) {
        this.number = number;
        this.name = name;
        this.previous = previous;
    }

    /**
     * <p>
     * Update item.
     * </p>
     * 
     * @param item A target to update.
     */
    private Improvement update(Item item) {
        // update champion status
        item.improvement = new Improvement(this, item.improvement);

        // Chainable API
        return item.improvement;
    }

    /**
     * <p>
     * Update champion.
     * </p>
     * 
     * @param champion A target to update.
     */
    private Improvement update(Champion champion) {
        // update champion status
        champion.improvement = new Improvement(this, champion.improvement);

        // Chainable API
        return champion.improvement;
    }

    /**
     * <p>
     * Update rune.
     * </p>
     * 
     * @param rune A target to update.
     */
    private Improvement update(Rune rune) {
        // update champion status
        rune.improvement = new Improvement(this, rune.improvement);

        // Chainable API
        return rune.improvement;
    }

    /** The patch. */
    public static Patch P0000 = new Patch(1510, 2012, 11, 13, "Initial", null);

    static {
        P0000.update(Rune.VitalityMark).health(0, 0.54);
        P0000.update(Rune.VitalitySeal).health(0, 1.08);
        P0000.update(Rune.VitalityGlyph).health(0, 0.54);
        P0000.update(Rune.VitalityQuintessence).health(0, 2.7);

        // ====================================================
        // Item Definitions
        // ====================================================
        P0000.update(Item.AbyssalScepter).build(Item.BlastingWand, Item.NegatronCloak).cost(980).mr(45).ap(70);
        P0000.update(Item.AegisOftheLegion)
                .build(Item.EmblemOfValor, Item.NullMagicMantle, Item.RubyCrystal)
                .cost(625)
                .mr(20)
                .health(250);
        P0000.update(Item.AmplifyingTome).cost(435).ap(20);
        P0000.update(Item.ArchangelsStaff)
                .build(Item.TearOftheGoddess, Item.BlastingWand)
                .cost(1140)
                .ap(60)
                .mana(250)
                .mreg(10);
        P0000.update(Item.AthenesUnholyGrail)
                .build(Item.ChaliceOfHarmony, Item.FiendishCodex)
                .cost(920)
                .mr(40)
                .ap(60)
                .mreg(15);
        P0000.update(Item.AtmasImpaler).build(Item.AvariceBlade, Item.ChainVest).cost(780).critical(15);
        P0000.update(Item.AugmentDeath).build(Item.TheHexCore).cost(1000).ap(45);
        P0000.update(Item.AugmentGravity).build(Item.TheHexCore).cost(1000).mana(200).cdr(10).mreg(5);
        P0000.update(Item.AugmentPower).build(Item.TheHexCore).cost(1000).hreg(6).health(220);
        P0000.update(Item.AvariceBlade).build(Item.BrawlersGloves).cost(400).critical(10);
        P0000.update(Item.BFSword).cost(1550).ad(45);
        P0000.update(Item.BannerOfCommand).build(Item.BlastingWand, Item.EmblemOfValor).cost(890).ap(40).cdr(10);
        P0000.update(Item.BansheesVeil)
                .build(Item.NegatronCloak, Item.CatalystTheProtector)
                .cost(600)
                .mr(45)
                .mana(300)
                .health(400);
        P0000.update(Item.BerserkersGreaves).build(Item.BootsOfSpeed, Item.Dagger).cost(150).as(20);
        P0000.update(Item.BilgewaterCutlass).build(Item.Pickaxe, Item.VampiricScepter).cost(250).ls(10).ad(40);
        P0000.update(Item.BlackfireTorch)
                .build(Item.KagesLuckyPick, Item.FiendishCodex, Item.HauntingGuise)
                .cost(700)
                .ap(80)
                .health(250)
                .mreg(10);
        P0000.update(Item.BladeOftheRuinedKing).build(Item.BilgewaterCutlass).cost(975).ls(10).ad(40);
        P0000.update(Item.BlastingWand).cost(860).ap(40);
        P0000.update(Item.BonetoothNecklace).cost(800).ad(5);
        P0000.update(Item.BootsOfMobility).build(Item.BootsOfSpeed).cost(650);
        P0000.update(Item.BootsOfSpeed).cost(350);
        P0000.update(Item.BootsOfSwiftness).build(Item.BootsOfSpeed).cost(650);
        P0000.update(Item.BrawlersGloves).cost(400).critical(8);
        P0000.update(Item.CatalystTheProtector)
                .build(Item.RubyCrystal, Item.SapphireCrystal)
                .cost(325)
                .mana(300)
                .health(200);
        P0000.update(Item.ChainVest).cost(720);
        P0000.update(Item.ChaliceOfHarmony).build(Item.FaerieCharm, Item.NullMagicMantle).cost(300).mr(25).mreg(7);
        P0000.update(Item.CloakOfAgility).cost(730).critical(15);
        P0000.update(Item.ClothArmor).cost(300);
        P0000.update(Item.CrystallineFlask).cost(345);
        P0000.update(Item.Dagger).cost(400).as(12);
        P0000.update(Item.DeathfireGrasp).build(Item.AmplifyingTome, Item.NeedlesslyLargeRod).cost(965).ap(100).cdr(15);
        P0000.update(Item.DoransBlade).cost(475).ad(10).health(80);
        P0000.update(Item.DoransRing).cost(475).ap(15).health(80).mreg(3);
        P0000.update(Item.DoransShield).cost(475).hreg(5).health(100);
        P0000.update(Item.EleisasMiracle).build(Item.PhilosophersStone).cost(400).hreg(10).mreg(15);
        P0000.update(Item.ElixirOfBrilliance).cost(250);
        P0000.update(Item.ElixirOfFortitude).cost(250);
        P0000.update(Item.EmblemOfValor).build(Item.ClothArmor, Item.RejuvenationBead).cost(170);
        P0000.update(Item.Entropy).build(Item.BFSword, Item.Phage).cost(600).ad(70).health(275);
        P0000.update(Item.ExecutionersCalling).build(Item.AvariceBlade, Item.LongSword).cost(700).critical(15).ad(25);
        P0000.update(Item.FaerieCharm).cost(180).mreg(3);
        P0000.update(Item.FiendishCodex).build(Item.FaerieCharm, Item.AmplifyingTome).cost(385).ap(30).mreg(6);
        P0000.update(Item.FrozenHeart).build(Item.GlacialShroud, Item.WardensMail).cost(500).mana(400).cdr(20);
        P0000.update(Item.FrozenMallet).build(Item.Phage, Item.GiantsBelt).cost(835).ad(30).health(700);
        P0000.update(Item.GiantsBelt).cost(1000).health(400);
        P0000.update(Item.GlacialShroud).build(Item.SapphireCrystal, Item.ChainVest).cost(380).mana(300);
        P0000.update(Item.GrezsSpectralLantern).build(Item.ClothArmor, Item.VampiricScepter).cost(150).ls(12).ad(20);
        P0000.update(Item.GuardianAngel).build(Item.NullMagicMantle, Item.ChainVest).cost(1480).mr(30);
        P0000.update(Item.GuinsoosRageblade).build(Item.BlastingWand, Item.Pickaxe).cost(865).ap(40).ad(30);
        P0000.update(Item.HauntingGuise).build(Item.RubyCrystal, Item.AmplifyingTome).cost(575).ap(25).health(200);
        P0000.update(Item.HealthPotion).cost(35);
        P0000.update(Item.Hexdrinker).build(Item.LongSword, Item.NullMagicMantle).cost(550).mr(25).ad(25);
        P0000.update(Item.HextechGunblade)
                .build(Item.BilgewaterCutlass, Item.HextechRevolver)
                .cost(275)
                .ls(10)
                .ap(65)
                .ad(45);
        P0000.update(Item.HextechRevolver).build(Item.AmplifyingTome, Item.AmplifyingTome).cost(330).ap(40);
        P0000.update(Item.HextechSweeper)
                .build(Item.AmplifyingTome, Item.AmplifyingTome, Item.Kindlegem)
                .cost(200)
                .ap(50)
                .health(300);
        P0000.update(Item.HuntersMachete).cost(300);
        P0000.update(Item.IcebornGauntlet).build(Item.Sheen, Item.GlacialShroud).cost(640).ap(40).mana(500).cdr(15);
        P0000.update(Item.IchorOfIllumination).cost(500);
        P0000.update(Item.IchorOfRage).cost(500);
        P0000.update(Item.InfinityEdge)
                .build(Item.BFSword, Item.CloakOfAgility, Item.Pickaxe)
                .cost(645)
                .critical(25)
                .ad(70);
        P0000.update(Item.IonianBootsOfLucidity).build(Item.BootsOfSpeed).cost(700);
        P0000.update(Item.KagesLuckyPick).build(Item.AmplifyingTome).cost(330).ap(25);
        P0000.update(Item.Kindlegem).build(Item.RubyCrystal).cost(375).health(200);
        P0000.update(Item.KitaesBloodrazor).build(Item.Pickaxe, Item.RecurveBow).cost(700).as(40).ad(30);
        P0000.update(Item.LastWhisper).build(Item.LongSword, Item.Pickaxe).cost(1025).ad(40);
        P0000.update(Item.LiandrysTorment).build(Item.HauntingGuise, Item.AmplifyingTome).cost(980).ap(60).health(300);
        P0000.update(Item.LichBane).build(Item.Sheen, Item.BlastingWand).cost(880).ap(80).ms(5).mana(250);
        P0000.update(Item.LocketOftheIronSolari)
                .build(Item.Kindlegem, Item.ClothArmor, Item.RejuvenationBead)
                .cost(670)
                .cdr(10)
                .health(425);
        P0000.update(Item.LongSword).cost(400).ad(10);
        P0000.update(Item.MadredsRazors).build(Item.ClothArmor, Item.HuntersMachete).cost(100);
        P0000.update(Item.Malady).build(Item.Dagger, Item.Dagger, Item.AmplifyingTome).cost(800).ap(25).as(45);
        P0000.update(Item.ManaManipulator).build(Item.FaerieCharm, Item.FaerieCharm).cost(40);
        P0000.update(Item.Manamune).build(Item.TearOftheGoddess, Item.LongSword).cost(1000).ad(20).mana(250).mreg(7);
        P0000.update(Item.ManaPotion).cost(35);
        P0000.update(Item.MawOfMalmortius).build(Item.Hexdrinker, Item.Pickaxe).cost(975).mr(36).ad(55);
        P0000.update(Item.MejaisSoulstealer).build(Item.AmplifyingTome).cost(800).ap(20);
        P0000.update(Item.MercurialScimitar).build(Item.QuicksilverSash, Item.BFSword).cost(600).mr(45).ad(60);
        P0000.update(Item.MercurysTreads).build(Item.BootsOfSpeed, Item.NullMagicMantle).cost(450).mr(25);
        P0000.update(Item.MikaelsCrucible)
                .build(Item.SapphireCrystal, Item.ChaliceOfHarmony)
                .cost(920)
                .mr(40)
                .mana(300)
                .mreg(9);
        P0000.update(Item.Morellonomicon)
                .build(Item.FiendishCodex, Item.KagesLuckyPick)
                .cost(435)
                .ap(75)
                .cdr(20)
                .mreg(12);
        P0000.update(Item.Muramana).build(Item.Manamune).cost(0).ad(20).mana(1000).mreg(7);
        P0000.update(Item.NashorsTooth).build(Item.Stinger, Item.FiendishCodex).cost(250).ap(65).as(50).mreg(10);
        P0000.update(Item.NeedlesslyLargeRod).cost(1600).ap(80);
        P0000.update(Item.NegatronCloak).cost(810).mr(45);
        P0000.update(Item.NinjaTabi).build(Item.BootsOfSpeed, Item.ClothArmor).cost(350);
        P0000.update(Item.NullMagicMantle).cost(400).mr(20);
        P0000.update(Item.OdynsVeil)
                .build(Item.NegatronCloak, Item.CatalystTheProtector)
                .cost(600)
                .mr(50)
                .mana(350)
                .health(350);
        P0000.update(Item.Ohmwrecker).build(Item.CatalystTheProtector, Item.ChainVest).cost(930).mana(300).health(350);
        P0000.update(Item.OraclesElixir).cost(400);
        P0000.update(Item.OraclesExtract).cost(250);
        P0000.update(Item.OverlordsBloodmail).build(Item.GiantsBelt, Item.RubyCrystal).cost(980).health(850);
        P0000.update(Item.Phage).build(Item.RubyCrystal, Item.LongSword).cost(590).ad(20).health(200);
        P0000.update(Item.PhantomDancer)
                .build(Item.CloakOfAgility, Item.Zeal, Item.Dagger)
                .cost(495)
                .critical(30)
                .ms(5)
                .as(50);
        P0000.update(Item.PhilosophersStone).build(Item.FaerieCharm, Item.RejuvenationBead).cost(340).hreg(7).mreg(9);
        P0000.update(Item.Pickaxe).cost(875).ad(25);
        P0000.update(Item.ProspectorsBlade).cost(950).ls(5).ad(20);
        P0000.update(Item.ProspectorsRing).cost(950).ap(40);
        P0000.update(Item.QuicksilverSash).build(Item.NegatronCloak).cost(850).mr(45);
        P0000.update(Item.RabadonsDeathcap).build(Item.BlastingWand, Item.NeedlesslyLargeRod).cost(740).ap(120);
        P0000.update(Item.RanduinsOmen).build(Item.GiantsBelt, Item.WardensMail).cost(1000).health(500);
        P0000.update(Item.RavenousHydra).build(Item.Tiamat, Item.VampiricScepter).cost(400).ls(10).ad(75).hreg(15);
        P0000.update(Item.RecurveBow).cost(950).as(30);
        P0000.update(Item.RejuvenationBead).cost(180).hreg(5);
        P0000.update(Item.RodOfAges)
                .build(Item.CatalystTheProtector, Item.BlastingWand)
                .cost(740)
                .ap(60)
                .mana(450)
                .health(450);
        P0000.update(Item.RubyCrystal).cost(475).health(180);
        P0000.update(Item.RubySightstone).build(Item.Sightstone).cost(125).health(300);
        P0000.update(Item.RunaansHurricane).build(Item.Dagger, Item.RecurveBow, Item.Dagger).cost(1000).as(70);
        P0000.update(Item.RunicBulwark).build(Item.NullMagicMantle, Item.AegisOftheLegion).cost(650).mr(30).health(400);
        P0000.update(Item.RylaisCrystalScepter)
                .build(Item.BlastingWand, Item.AmplifyingTome, Item.GiantsBelt)
                .cost(605)
                .ap(80)
                .health(500);
        P0000.update(Item.SanguineBlade).build(Item.BFSword, Item.VampiricScepter).cost(500).ls(15).ad(65);
        P0000.update(Item.SapphireCrystal).cost(400).mana(200);
        P0000.update(Item.SeraphsEmbrace).build(Item.ArchangelsStaff).cost(0).ap(60).mana(1000).mreg(10);
        P0000.update(Item.ShardOfTrueIce).build(Item.KagesLuckyPick, Item.ManaManipulator).cost(535).ap(45);
        P0000.update(Item.Sheen).build(Item.SapphireCrystal, Item.AmplifyingTome).cost(425).ap(25).mana(200);
        P0000.update(Item.ShurelyasReverie)
                .build(Item.Kindlegem, Item.PhilosophersStone)
                .cost(550)
                .hreg(10)
                .health(250)
                .mreg(10);
        P0000.update(Item.Sightstone).cost(700).health(100);
        P0000.update(Item.SightWard).cost(75);
        P0000.update(Item.SorcerersShoes).build(Item.BootsOfSpeed).cost(750);
        P0000.update(Item.SpiritOftheAncientGolem)
                .build(Item.SpiritStone, Item.GiantsBelt)
                .cost(600)
                .hreg(14)
                .health(500)
                .mreg(7);
        P0000.update(Item.SpiritOftheElderLizard)
                .build(Item.SpiritStone, Item.Pickaxe)
                .cost(725)
                .ad(50)
                .hreg(14)
                .cdr(10)
                .mreg(7);
        P0000.update(Item.SpiritOftheSpectralWraith)
                .build(Item.SpiritStone, Item.HextechRevolver)
                .cost(400)
                .ap(50)
                .cdr(10)
                .mreg(10);
        P0000.update(Item.SpiritStone)
                .build(Item.HuntersMachete, Item.FaerieCharm, Item.RejuvenationBead)
                .cost(140)
                .hreg(14)
                .mreg(7);
        P0000.update(Item.SpiritVisage).build(Item.Kindlegem, Item.NegatronCloak).cost(540).mr(50).cdr(15).health(200);
        P0000.update(Item.StatikkShiv).build(Item.Zeal, Item.AvariceBlade).cost(525).critical(20).ms(6).as(40);
        P0000.update(Item.Stinger).build(Item.Dagger, Item.Dagger).cost(450).as(40);
        P0000.update(Item.SunfireCape).build(Item.ChainVest, Item.GiantsBelt).cost(780).health(450);
        P0000.update(Item.SwordOftheDivine).build(Item.RecurveBow, Item.Dagger).cost(850).as(45);
        P0000.update(Item.SwordOftheOccult).build(Item.LongSword).cost(800).ad(10);
        P0000.update(Item.TearOftheGoddess).build(Item.SapphireCrystal, Item.FaerieCharm).cost(120).mana(250).mreg(7);
        P0000.update(Item.TheBlackCleaver)
                .build(Item.TheBrutalizer, Item.RubyCrystal)
                .cost(1188)
                .ad(50)
                .cdr(10)
                .health(250);
        P0000.update(Item.TheBloodthirster).build(Item.BFSword, Item.VampiricScepter).cost(650).ls(12).ad(70);
        P0000.update(Item.TheBrutalizer).build(Item.LongSword, Item.LongSword).cost(537).ad(25);
        P0000.update(Item.TheHexCore).cost(0);
        P0000.update(Item.TheLightbringer).build(Item.BonetoothNecklace, Item.Pickaxe).cost(300).ls(12).ad(50);
        P0000.update(Item.Thornmail).build(Item.ChainVest, Item.ClothArmor).cost(1180);
        P0000.update(Item.Tiamat)
                .build(Item.Pickaxe, Item.LongSword, Item.RejuvenationBead, Item.RejuvenationBead)
                .cost(665)
                .ad(50)
                .hreg(15);
        P0000.update(Item.TrinityForce)
                .build(Item.Zeal, Item.Sheen, Item.Phage)
                .cost(300)
                .critical(10)
                .ap(30)
                .ms(8)
                .mana(200)
                .as(30)
                .ad(30)
                .health(250);
        P0000.update(Item.TwinShadows).build(Item.KagesLuckyPick, Item.NullMagicMantle).cost(735).ap(40).ms(6);
        P0000.update(Item.VampiricScepter).build(Item.LongSword).cost(400).ls(10).ad(10);
        P0000.update(Item.VisionWard).cost(125);
        P0000.update(Item.VoidStaff).build(Item.BlastingWand, Item.AmplifyingTome).cost(1000).ap(70);
        P0000.update(Item.WardensMail).build(Item.ClothArmor, Item.ClothArmor).cost(500);
        P0000.update(Item.WarmogsArmor)
                .build(Item.GiantsBelt, Item.RubyCrystal, Item.RejuvenationBead)
                .cost(995)
                .health(1000);
        P0000.update(Item.WillOftheAncients).build(Item.KagesLuckyPick, Item.HextechRevolver).cost(585).ap(50);
        P0000.update(Item.WitsEnd).build(Item.RecurveBow, Item.NullMagicMantle).cost(850).mr(20).as(40);
        P0000.update(Item.WoogletsWitchcap)
                .build(Item.BlastingWand, Item.BlastingWand, Item.ChainVest)
                .cost(1060)
                .ap(100);
        P0000.update(Item.WrigglesLantern).build(Item.VampiricScepter, Item.MadredsRazors).cost(100).ls(10).ad(15);
        P0000.update(Item.YoumuusGhostblade)
                .build(Item.AvariceBlade, Item.TheBrutalizer)
                .cost(563)
                .critical(15)
                .ad(30)
                .cdr(10);
        P0000.update(Item.Zeal).build(Item.BrawlersGloves, Item.Dagger).cost(375).critical(10).ms(5).as(18);
        P0000.update(Item.ZekesHerald).build(Item.VampiricScepter, Item.Kindlegem).cost(800).cdr(15).health(250);
        P0000.update(Item.Zephyr).build(Item.Stinger, Item.LongSword).cost(1200).ms(10).as(50).ad(20).cdr(10);
        P0000.update(Item.ZhonyasHourglass).build(Item.NeedlesslyLargeRod, Item.ChainVest).cost(780).ap(100);

        // =============================================================
        // Champion Definitions
        // =============================================================
        P0000.update(Champion.Ahri)
                .health(380, 80)
                .hreg(5.5, 0.6)
                .mana(230, 50)
                .mreg(6.25, 0.6)
                .ad(50, 3)
                .as(0.668, 2)
                .ar(10, 3.5)
                .mr(30, 0)
                .range(550)
                .ms(330);
        P0000.update(Champion.Akali)
                .health(445, 85)
                .hreg(7.25, 0.65)
                .energy(200)
                .ereg(50)
                .ad(53, 3.2)
                .as(0.694, 3.1)
                .ar(16.5, 3.5)
                .mr(30, 1.25)
                .range(125)
                .ms(350);
        P0000.update(Champion.Alistar)
                .health(442, 102)
                .hreg(7.25, 0.85)
                .mana(215, 38)
                .mreg(6.45, 0.45)
                .ad(55.03, 3.62)
                .as(0.625, 3.62)
                .ar(14.5, 3.5)
                .mr(30, 1.25)
                .range(125)
                .ms(325);
        P0000.update(Champion.Amumu)
                .health(472, 84)
                .hreg(7.45, 0.85)
                .mana(220, 40)
                .mreg(6.5, 0.525)
                .ad(47, 3.8)
                .as(0.638, 2.18)
                .ar(18, 3.3)
                .mr(30, 1.25)
                .range(125)
                .ms(335);
        P0000.update(Champion.Anivia)
                .health(350, 70)
                .hreg(4.65, 0.55)
                .mana(257, 53)
                .mreg(7.0, 0.6)
                .ad(48, 3.2)
                .as(0.625, 1.68)
                .ar(10.5, 4)
                .mr(30, 0)
                .range(600)
                .ms(325);
        P0000.update(Champion.Annie)
                .health(384, 76)
                .hreg(4.5, 0.55)
                .mana(250, 50)
                .mreg(6.9, 0.6)
                .ad(49, 2.625)
                .as(0.579, 1.36)
                .ar(12.5, 4)
                .mr(30, 0)
                .range(625)
                .ms(335);
        P0000.update(Champion.Ashe)
                .health(395, 79)
                .hreg(4.5, 0.55)
                .mana(173, 35)
                .mreg(6.3, 0.4)
                .ad(46.3, 2.85)
                .as(0.658, 3.34)
                .ar(11.5, 3.4)
                .mr(30, 0)
                .range(600)
                .ms(325);
        P0000.update(Champion.Blitzcrank)
                .health(423, 95)
                .hreg(7.25, 0.75)
                .mana(260, 40)
                .mreg(6.6, 0.5)
                .ad(55.66, 3.5)
                .as(0.625, 1.13)
                .ar(14.5, 3.5)
                .mr(30, 1.25)
                .range(125)
                .ms(325);
        P0000.update(Champion.Brand)
                .health(380, 76)
                .hreg(4.5, 0.55)
                .mana(250, 45)
                .mreg(7, 0.6)
                .ad(51.66, 3)
                .as(0.625, 1.36)
                .ar(12, 3.5)
                .mr(30, 0)
                .range(550)
                .ms(340);
        P0000.update(Champion.Caitlyn)
                .health(390, 80)
                .hreg(4.75, 0.55)
                .mana(255, 35)
                .mreg(6.5, 0.55)
                .ad(47, 3)
                .as(0.668, 3)
                .ar(13, 3.5)
                .mr(30, 0)
                .range(650)
                .ms(325);
        P0000.update(Champion.Cassiopeia)
                .health(380, 75)
                .hreg(4.85, 0.5)
                .mana(250, 50)
                .mreg(7.1, 0.75)
                .ad(47, 3.2)
                .as(0.644, 1.68)
                .ar(11.5, 4)
                .mr(30, 0)
                .range(550)
                .ms(335);
        P0000.update(Champion.Chogath)
                .health(440, 80)
                .hreg(7.5, 0.85)
                .mana(205, 40)
                .mreg(6.45, 0.45)
                .ad(54.1, 4.2)
                .as(0.625, 1.44)
                .ar(19, 3.5)
                .mr(30, 1.25)
                .range(125)
                .ms(345);
        P0000.update(Champion.Corki)
                .health(375, 82)
                .hreg(4.5, 0.55)
                .mana(243, 37)
                .mreg(6.5, 0.55)
                .ad(48.2, 3)
                .as(0.658, 2.3)
                .ar(13.5, 3.5)
                .mr(30, 0)
                .range(550)
                .ms(325);
        P0000.update(Champion.Darius)
                .health(426, 93)
                .hreg(8.25, 0.95)
                .mana(200, 37.5)
                .mreg(6, 0.35)
                .ad(50, 3.5)
                .as(0.679, 2.6)
                .ar(20, 3.5)
                .mr(30, 1.25)
                .range(125)
                .ms(340);
        P0000.update(Champion.Diana)
                .health(438, 90)
                .hreg(7, 0.85)
                .mana(230, 40)
                .mreg(7, 0.6)
                .ad(48, 3)
                .as(0.625, 2.25)
                .ar(16, 3.6)
                .mr(30, 1.25)
                .range(150)
                .ms(345);
        P0000.update(Champion.DrMundo)
                .health(433, 89)
                .hreg(6.5, 0.75)
                .ad(56.23, 3)
                .as(0.625, 2.8)
                .ar(17, 3.5)
                .mr(30, 1.25)
                .range(125)
                .ms(345);
        P0000.update(Champion.Draven)
                .health(420, 82)
                .hreg(5, 0.7)
                .mana(240, 42)
                .mreg(6.95, 0.65)
                .ad(46.5, 3.5)
                .as(0.679, 2.6)
                .ar(16, 3.3)
                .mr(30, 0)
                .range(550)
                .ms(330);
        P0000.update(Champion.Elise)
                .health(395, 80)
                .hreg(4.7, 0.6)
                .mana(240, 50)
                .mreg(6.8, 0.65)
                .ad(47.5, 3)
                .as(0.625, 1.75)
                .ar(12.65, 3.35)
                .mr(30, 0)
                .range(550)
                .ms(335);
        P0000.update(Champion.Evelynn)
                .health(414, 86)
                .hreg(6.95, 0.55)
                .mana(180, 42)
                .mreg(7.1, 0.6)
                .ad(48, 3.3)
                .as(0.658, 3.84)
                .ar(12.5, 4)
                .mr(30, 1.25)
                .range(125)
                .ms(340);
        P0000.update(Champion.Ezreal)
                .health(350, 80)
                .hreg(5.5, 0.55)
                .mana(235, 45)
                .mreg(7, 0.65)
                .ad(47.2, 3)
                .as(0.665, 2.8)
                .ar(12, 3.5)
                .mr(30, 0)
                .range(550)
                .ms(330);
        P0000.update(Champion.Fiddlesticks)
                .health(390, 80)
                .hreg(4.6, 0.6)
                .mana(251, 59)
                .mreg(6.9, 0.65)
                .ad(45.95, 2.625)
                .as(0.625, 2.11)
                .ar(11, 3.5)
                .mr(30, 0)
                .range(480)
                .ms(335);
        P0000.update(Champion.Fiora)
                .health(450, 85)
                .hreg(6.3, 0.8)
                .mana(220, 40)
                .mreg(7.25, 0.5)
                .ad(54.5, 3.2)
                .as(0.672, 3)
                .ar(15.5, 3.5)
                .mr(30, 1.25)
                .range(125)
                .ms(350);
        P0000.update(Champion.Fizz)
                .health(414, 86)
                .hreg(7.0, 0.7)
                .mana(200, 40)
                .mreg(6.15, 0.45)
                .ad(53, 3)
                .as(0.658, 3.1)
                .ar(13, 3.4)
                .mr(30, 1.25)
                .range(175)
                .ms(335);
        P0000.update(Champion.Galio)
                .health(435, 85)
                .hreg(7.45, 0.75)
                .mana(235, 50)
                .mreg(7, 0.7)
                .ad(56.3, 3.375)
                .as(0.638, 1.2)
                .ar(17, 3.5)
                .mr(30, 0)
                .range(125)
                .ms(335);
        P0000.update(Champion.Gangplank)
                .health(495, 81)
                .hreg(425, 0.75)
                .mana(215, 40)
                .mreg(6.5, 0.7)
                .ad(54, 3)
                .as(0.651, 2.75)
                .ar(16.5, 3.3)
                .mr(30, 1.25)
                .range(125)
                .ms(345);
        P0000.update(Champion.Garen)
                .health(455, 96)
                .hreg(7.5, 0.75)
                .ad(52.5, 3.5)
                .as(0.625, 2.9)
                .ar(19, 2.7)
                .mr(30, 1.25)
                .range(125)
                .ms(345);
        P0000.update(Champion.Gragas)
                .health(434, 89)
                .hreg(7.25, 0.85)
                .mana(221, 47)
                .mreg(6.45, 0.45)
                .ad(55.78, 3.375)
                .as(0.651, 2.05)
                .ar(16, 3.6)
                .mr(30, 0)
                .range(125)
                .ms(340);
        P0000.update(Champion.Graves)
                .health(410, 84)
                .hreg(5.5, 0.7)
                .mana(255, 40)
                .mreg(6.75, 0.7)
                .ad(51, 3.1)
                .as(0.625, 2.9)
                .ar(15, 3.2)
                .mr(30, 0)
                .range(525)
                .ms(330);
        P0000.update(Champion.Hecarim)
                .health(440, 95)
                .hreg(8, 0.75)
                .mana(210, 40)
                .mreg(6.5, 0.6)
                .ad(56, 3.2)
                .as(0.67, 2.5)
                .ar(16, 4)
                .mr(30, 1.25)
                .range(175)
                .ms(345);
        P0000.update(Champion.Heimerdinger)
                .health(350, 75)
                .hreg(4.5, 0.55)
                .mana(240, 65)
                .mreg(7, 0.65)
                .ad(49.24, 3)
                .as(0.625, 1.21)
                .ar(7, 3)
                .mr(30, 0)
                .range(550)
                .ms(325);
        P0000.update(Champion.Irelia)
                .health(456, 90)
                .hreg(7.5, 0.65)
                .mana(230, 35)
                .mreg(7, 0.65)
                .ad(56, 3.3)
                .as(0.665, 3.2)
                .ar(15, 3.75)
                .mr(30, 1.25)
                .range(125)
                .ms(345);
        P0000.update(Champion.Janna)
                .health(356, 78)
                .hreg(4.5, 0.55)
                .mana(302, 64)
                .mreg(6.9, 0.6)
                .ad(49, 2.95)
                .as(0.625, 2.61)
                .ar(9, 3.8)
                .mr(30, 0)
                .range(475)
                .ms(335);
        P0000.update(Champion.JarvanIV)
                .health(420, 90)
                .hreg(7, 0.7)
                .mana(235, 40)
                .mreg(6, 0.45)
                .ad(50, 3.4)
                .as(0.658, 2.5)
                .ar(14, 3)
                .mr(30, 1.25)
                .range(175)
                .ms(340);
        P0000.update(Champion.Jax)
                .health(463, 98)
                .hreg(7.45, 0.55)
                .mana(230, 35)
                .mreg(6.4, 0.7)
                .ad(56.3, 3.375)
                .as(0.638, 3.4)
                .ar(18, 3.5)
                .mr(30, 1.25)
                .range(125)
                .ms(350);
        P0000.update(Champion.Jayce)
                .health(420, 90)
                .hreg(6, 0.8)
                .mana(240, 40)
                .mreg(7, 0.7)
                .ad(46.5, 3.5)
                .as(0.658, 3)
                .ar(12.5, 3.5)
                .mr(30, 0)
                .range(125)
                .ms(335);
        P0000.update(Champion.Karma)
                .health(410, 86)
                .hreg(4.7, 0.55)
                .mana(240, 60)
                .mreg(6.8, 0.65)
                .ad(50, 3.3)
                .as(0.625, 2.3)
                .ar(15, 3.5)
                .mr(30, 0)
                .range(425)
                .ms(335);
        P0000.update(Champion.Karthus)
                .health(390, 75)
                .hreg(5.5, 0.55)
                .mana(270, 61)
                .mreg(6.5, 0.6)
                .ad(42.2, 3.25)
                .as(0.625, 2.11)
                .ar(11, 3.5)
                .mr(30, 0)
                .range(450)
                .ms(335);
        P0000.update(Champion.Kassadin)
                .health(433, 78)
                .hreg(6.95, 0.5)
                .mana(230, 45)
                .mreg(6.9, 0.6)
                .ad(52.3, 3.9)
                .as(0.638, 3.7)
                .ar(14, 3.2)
                .mr(30, 1.25)
                .range(125)
                .ms(340);
        P0000.update(Champion.Katarina)
                .health(395, 83)
                .hreg(6.95, 0.55)
                .ad(53, 3.2)
                .as(0.658, 2.74)
                .ar(14.75, 4)
                .mr(30, 1.25)
                .range(125)
                .ms(350);
        P0000.update(Champion.Kayle)
                .health(418, 93)
                .hreg(7, 0.75)
                .mana(255, 40)
                .mreg(6.9, 0.525)
                .ad(53.3, 2.8)
                .as(0.638, 2.5)
                .ar(17, 3.5)
                .mr(30, 0.75)
                .range(125)
                .ms(335);
        P0000.update(Champion.Kennen)
                .health(403, 79)
                .hreg(4.65, 0.65)
                .energy(200)
                .ereg(50)
                .ad(51.3, 3.3)
                .as(0.69, 3.4)
                .ar(14, 3.75)
                .mr(30, 0)
                .range(550)
                .ms(335);
        P0000.update(Champion.KhaZix)
                .health(430, 85)
                .hreg(6.25, 0.75)
                .mana(260, 40)
                .mreg(6.75, 0.5)
                .ad(50, 3.1)
                .as(0.665, 2.7)
                .ar(15, 3)
                .mr(30, 1.25)
                .range(125)
                .ms(350);
        P0000.update(Champion.KogMaw)
                .health(440, 84)
                .hreg(5, 0.55)
                .mana(295, 40)
                .mreg(7.5, 0.7)
                .ad(46, 3)
                .as(0.665, 2.65)
                .ar(13, 3.53)
                .mr(30, 0)
                .range(500)
                .ms(340);
        P0000.update(Champion.LeBlanc)
                .health(390, 75)
                .hreg(4.5, 0.55)
                .mana(250, 50)
                .mreg(6.9, 0.6)
                .ad(51, 3.1)
                .as(0.625, 1.4)
                .ar(12, 3.5)
                .mr(30, 0)
                .range(525)
                .ms(335);
        P0000.update(Champion.LeeSin)
                .health(428, 85)
                .hreg(6.25, 075)
                .ereg(200)
                .ereg(50)
                .ad(55.8, 3.2)
                .as(0.651, 3)
                .ar(16, 3.7)
                .mr(30, 1.25)
                .range(125)
                .ms(350);
        P0000.update(Champion.Leona)
                .health(430, 87)
                .hreg(9, 0.85)
                .mana(235, 40)
                .mreg(8, 0.7)
                .ad(55, 3)
                .as(0.625, 2.9)
                .ar(18, 3.1)
                .mr(30, 1.25)
                .range(125)
                .ms(335);
        P0000.update(Champion.Lulu)
                .health(415, 82)
                .hreg(6, 0.72)
                .mana(200, 50)
                .mreg(6, 0.6)
                .ad(44.4, 2.6)
                .as(0.625, 2.2)
                .ar(9, 3.7)
                .mr(30, 0)
                .range(550)
                .ms(325);
        P0000.update(Champion.Lux)
                .health(345, 79)
                .hreg(4.5, 0.55)
                .mana(250, 50)
                .mreg(6, 0.6)
                .ad(50, 3.3)
                .as(0.625, 1.36)
                .ar(8, 4)
                .mr(30, 0)
                .range(550)
                .ms(340);
        P0000.update(Champion.Malphite)
                .health(423, 90)
                .hreg(7.45, 0.55)
                .mana(215, 40)
                .mreg(6.4, 0.55)
                .ad(56.3, 3.375)
                .as(0.638, 3.4)
                .ar(18, 3.75)
                .mr(30, 1.25)
                .range(125)
                .ms(335);
        P0000.update(Champion.Malzahar)
                .health(380, 80)
                .hreg(4.5, 0.55)
                .mana(250, 45)
                .mreg(7, 0.6)
                .ad(51.66, 3)
                .as(0.625, 1.36)
                .ar(15, 3.5)
                .mr(30, 0)
                .range(550)
                .ms(340);
        P0000.update(Champion.Maokai)
                .health(421, 90)
                .hreg(7.25, 0.85)
                .mana(250, 46)
                .mreg(6.45, 0.45)
                .ad(58, 3.3)
                .as(0.694, 2.13)
                .ar(18, 4)
                .mr(30, 0)
                .range(125)
                .ms(335);
        P0000.update(Champion.MasterYi)
                .health(444, 86)
                .hreg(6.75, 0.65)
                .mana(199, 36)
                .mreg(6.5, 0.45)
                .ad(55.12, 3.1)
                .as(0.679, 2.98)
                .ar(16.3, 3.7)
                .mr(30, 1.25)
                .range(125)
                .ms(355);
        P0000.update(Champion.MissFortune)
                .health(435, 85)
                .hreg(5.1, 0.65)
                .mana(212, 38)
                .mreg(6.95, 0.65)
                .ad(46.5, 3)
                .as(0.658, 3.01)
                .ar(15, 3)
                .mr(30, 0)
                .range(550)
                .ms(325);
        P0000.update(Champion.Mordekaiser)
                .health(421, 80)
                .hreg(7.45, 0.55)
                .ad(51.7, 3.5)
                .as(0.694, 3)
                .ar(15, 3.5)
                .mr(30, 1.25)
                .range(125)
                .ms(340);
        P0000.update(Champion.Morgana)
                .health(403, 86)
                .hreg(4.7, 0.6)
                .mana(240, 60)
                .mreg(6.8, 0.65)
                .ad(51.58, 3.5)
                .as(0.579, 1.53)
                .ar(15, 3.8)
                .mr(30, 0)
                .range(425)
                .ms(335);
        P0000.update(Champion.Nami)
                .health(365, 74)
                .hreg(4.5, 055)
                .mana(305, 43)
                .mreg(6.9, 0.6)
                .ad(48, 3.1)
                .as(0.644, 2.6)
                .ar(9, 4)
                .mr(30, 0)
                .range(550)
                .ms(330);
        P0000.update(Champion.Nasus)
                .health(410, 90)
                .hreg(7.5, 0.9)
                .mana(200, 45)
                .mreg(6.6, 0.5)
                .ad(53.3, 3.5)
                .as(0.638, 3.48)
                .ar(15, 3.5)
                .mr(30, 1.25)
                .range(125)
                .ms(350);
        P0000.update(Champion.Nautilus)
                .health(432, 86)
                .hreg(7.45, 0.55)
                .mana(200, 50)
                .mreg(7.45, 0.7)
                .ad(52, 3.3)
                .as(0.613, 0.98)
                .ar(12, 3.25)
                .mr(30, 1.25)
                .range(175)
                .ms(325);
        P0000.update(Champion.Nidalee)
                .health(370, 90)
                .hreg(5.0, 0.6)
                .mana(220, 45)
                .mreg(7, 0.5)
                .ad(49, 3.5)
                .as(0.672, 3.22)
                .ar(11, 3.5)
                .mr(30, 10.75)
                .range(525)
                .ms(335);
        P0000.update(Champion.Nocturne)
                .health(430, 85)
                .hreg(7, 0.75)
                .mana(215, 35)
                .mreg(6, 0.45)
                .ad(54, 3.1)
                .as(0.668, 2.7)
                .ar(17, 3.5)
                .mr(30, 1.25)
                .range(125)
                .ms(345);
        P0000.update(Champion.Nunu)
                .health(437, 108)
                .hreg(7.05, 0.8)
                .mana(213, 42)
                .mreg(6.6, 0.5)
                .ad(51.6, 3.4)
                .as(0.625, 2.25)
                .ar(16.5, 3.5)
                .mr(30, 1.25)
                .range(125)
                .ms(340);
        P0000.update(Champion.Olaf)
                .health(441, 93)
                .hreg(7, 0.9)
                .mana(225, 45)
                .mreg(6.5, 0.575)
                .ad(54.1, 3.5)
                .as(0.694, 2.7)
                .ar(17, 3)
                .mr(30, 1.25)
                .range(125)
                .ms(350);
        P0000.update(Champion.Orianna)
                .health(385, 79)
                .hreg(5.95, 0.55)
                .mana(250, 50)
                .mreg(7, 0.5)
                .ad(44, 2.6)
                .as(0.658, 3.5)
                .ar(8, 3)
                .mr(30, 0)
                .range(525)
                .ms(325);
        P0000.update(Champion.Pantheon)
                .health(433, 87)
                .hreg(6.75, 0.65)
                .mana(210, 34)
                .mreg(6.6, 0.45)
                .ad(50.7, 2.9)
                .as(0.679, 2.95)
                .ar(17.1, 3.9)
                .mr(30, 1.25)
                .range(155)
                .ms(355);
        P0000.update(Champion.Poppy)
                .health(423, 81)
                .hreg(7.45, 0.55)
                .mana(185, 30)
                .mreg(6.4, 0.45)
                .ad(56.3, 3.375)
                .as(0.638, 3.35)
                .ar(18, 4)
                .mr(30, 0)
                .range(125)
                .ms(345);
        P0000.update(Champion.Rammus)
                .health(420, 86)
                .hreg(8, 0.55)
                .mana(255, 33)
                .mreg(4.5, 0.3)
                .ad(50, 3.5)
                .as(0.625, 2.22)
                .ar(21, 3.8)
                .mr(30, 1.25)
                .range(125)
                .ms(335);
        P0000.update(Champion.Renekton)
                .health(426, 87)
                .hreg(6.7, 0.75)
                .ad(53.12, 3.1)
                .as(0.665, 2.65)
                .ar(15.2, 3.8)
                .mr(30, 1.25)
                .range(125)
                .ms(345);
        P0000.update(Champion.Rengar)
                .health(435, 85)
                .hreg(4, 0.4)
                .ad(55, 3)
                .as(0.679, 2.85)
                .ar(16, 3.5)
                .mr(30, 1.25)
                .range(125)
                .ms(345);
        P0000.update(Champion.Riven)
                .health(414, 86)
                .hreg(10.4, 0.9)
                .ad(54, 2.75)
                .as(0.625, 3.5)
                .ar(15, 3.1)
                .mr(30, 1.25)
                .range(125)
                .ms(345);
        P0000.update(Champion.Rumble)
                .health(450, 80)
                .hreg(7, 0.7)
                .ad(55.32, 3.2)
                .as(0.644, 1.85)
                .ar(16, 3.5)
                .mr(30, 1.25)
                .range(125)
                .ms(345);
        P0000.update(Champion.Ryze)
                .health(360, 86)
                .hreg(4.35, 0.55)
                .mana(250, 55)
                .mreg(7, 0.6)
                .ad(52, 3)
                .as(0.625, 2.11)
                .ar(11, 3.9)
                .mr(30, 0)
                .range(550)
                .ms(335);
        P0000.update(Champion.Sejuani)
                .health(450, 85)
                .hreg(7.35, 0.85)
                .mana(220, 40)
                .mreg(6.45, 0.45)
                .ad(54, 3.4)
                .as(0.67, 1.45)
                .ar(20.5, 3.5)
                .mr(30, 1.25)
                .range(125)
                .ms(340);
        P0000.update(Champion.Shaco)
                .health(441, 84)
                .hreg(7.45, 0.55)
                .mana(270, 40)
                .mreg(6.4, 0.45)
                .ad(51.7, 3.5)
                .as(0.694, 3)
                .ar(15, 3.5)
                .mr(30, 1.25)
                .range(125)
                .ms(350);
        P0000.update(Champion.Shen)
                .health(428, 85)
                .hreg(7.45, 0.55)
                .energy(200)
                .ereg(50)
                .ad(54.5, 3.375)
                .as(0.651, 3.4)
                .ar(15, 4)
                .mr(30, 0)
                .range(125)
                .ms(335);
        P0000.update(Champion.Shyvana)
                .health(435, 95)
                .hreg(7.2, 0.8)
                .ad(54.5, 3.4)
                .as(0.658, 3.4)
                .ar(17.6, 3.4)
                .mr(30, 1.25)
                .range(125)
                .ms(350);
        P0000.update(Champion.Singed)
                .health(405, 82)
                .hreg(7.1, 0.55)
                .mana(215, 45)
                .mreg(6.6, 0.55)
                .ad(56.65, 3.375)
                .as(0.613, 1.81)
                .ar(18, 3.5)
                .mr(30, 0)
                .range(125)
                .ms(345);
        P0000.update(Champion.Sion)
                .health(403, 104)
                .hreg(7.9, 0.95)
                .mana(240, 40)
                .mreg(6.3, 0.4)
                .ad(55.52, 3.1875)
                .as(0.625, 1.63)
                .ar(17.75, 3.25)
                .mr(30, 1.25)
                .range(125)
                .ms(345);
        P0000.update(Champion.Sivir)
                .health(378, 82)
                .hreg(4.25, 0.55)
                .mana(203, 43)
                .mreg(6.5, 0.5)
                .ad(49, 2.9)
                .as(0.658, 3.28)
                .ar(12.75, 3.25)
                .mr(30, 0)
                .range(500)
                .ms(335);
        P0000.update(Champion.Skarner)
                .health(440, 96)
                .hreg(7.5, 0.85)
                .mana(205, 40)
                .mreg(6.45, 0.45)
                .ad(54.1, 4.2)
                .as(0.625, 2.1)
                .ar(19, 3.8)
                .mr(30, 1.25)
                .range(125)
                .ms(345);
        P0000.update(Champion.Sona)
                .health(340, 70)
                .hreg(4.5, 0.55)
                .mana(265, 45)
                .mreg(7.0, 0.65)
                .ad(47, 3)
                .as(0.644, 2.3)
                .ar(6, 3.3)
                .mr(30, 0)
                .range(550)
                .ms(330);
        P0000.update(Champion.Soraka)
                .health(375, 71)
                .hreg(4.5, 0.55)
                .mana(240, 60)
                .mreg(6.8, 0.65)
                .ad(48.8, 3)
                .as(0.625, 2.14)
                .ar(7.4, 3.8)
                .mr(30, 0)
                .range(550)
                .ms(335);
        P0000.update(Champion.Swain)
                .health(385, 78)
                .hreg(6.75, 0.65)
                .mana(240, 50)
                .mreg(6.8, 0.65)
                .ad(49, 3)
                .as(0.625, 2.11)
                .ar(12, 4)
                .mr(30, 0)
                .range(500)
                .ms(335);
        P0000.update(Champion.Syndra)
                .health(380, 78)
                .hreg(5.5, 0.6)
                .mana(250, 50)
                .mreg(6.9, 0.6)
                .ad(51, 2.9)
                .as(0.625, 2)
                .ar(15, 3.4)
                .mr(30, 0)
                .range(550)
                .ms(330);
        P0000.update(Champion.Talon)
                .health(440, 85)
                .hreg(7.25, 0.75)
                .mana(260, 40)
                .mreg(6.75, 0.5)
                .ad(50, 3.1)
                .as(0.668, 2.7)
                .ar(17, 3.5)
                .mr(30, 1.25)
                .range(125)
                .ms(350);
        P0000.update(Champion.Taric)
                .health(468, 90)
                .hreg(7.1, 0.5)
                .mana(255, 56)
                .mreg(4.1, 0.4)
                .ad(58, 3.5)
                .as(0.625, 2.02)
                .ar(16.5, 3.2)
                .mr(30, 1.25)
                .range(125)
                .ms(340);
        P0000.update(Champion.Teemo)
                .health(383, 82)
                .hreg(4.65, 0.65)
                .mana(200, 40)
                .mreg(6.45, 0.45)
                .ad(44.5, 3)
                .as(0.690, 3.38)
                .ar(14, 3.75)
                .mr(30, 0)
                .range(500)
                .ms(330);
        P0000.update(Champion.Tristana)
                .health(415, 82)
                .hreg(5.1, 0.65)
                .mana(193, 32)
                .mreg(6.45, 0.45)
                .ad(46.5, 3)
                .as(0.658, 3.01)
                .ar(15, 3)
                .mr(30, 0)
                .range(550)
                .ms(325);
        P0000.update(Champion.Trundle)
                .health(455, 96)
                .hreg(8, 0.85)
                .mana(206, 45)
                .mreg(6.9, 0.6)
                .ad(54.66, 3)
                .as(0.672, 2.9)
                .ar(19, 2.7)
                .mr(30, 1.25)
                .range(125)
                .ms(350);
        P0000.update(Champion.Tryndamere)
                .health(461, 98)
                .hreg(7.9, 0.9)
                .ad(56, 3.2)
                .as(0.644, 2.9)
                .ar(14.9, 3.1)
                .mr(30, 1.25)
                .range(125)
                .ms(345);
        P0000.update(Champion.TwistedFate)
                .health(384, 82)
                .hreg(4.5, 0.6)
                .mana(202, 38)
                .mreg(6.5, 0.5)
                .ad(46.61, 3.3)
                .as(0.651, 3.22)
                .ar(111.25, 3.15)
                .mr(30, 0)
                .range(525)
                .ms(330);
        P0000.update(Champion.Twitch)
                .health(389, 81)
                .hreg(5, 0.6)
                .mana(220, 40)
                .mreg(6.5, 0.45)
                .ad(49, 3)
                .as(0.679, 3.38)
                .ar(14, 3)
                .mr(30, 0)
                .range(550)
                .ms(330);
        P0000.update(Champion.Udyr)
                .health(427, 99)
                .hreg(7.45, 0.75)
                .mana(220, 30)
                .mreg(6.4, 0.45)
                .ad(52.91, 3.2)
                .as(0.658, 2.67)
                .ar(14.75, 4)
                .mr(30, 1.25)
                .range(125)
                .ms(345);
        P0000.update(Champion.Urgot)
                .health(437, 89)
                .hreg(5.5, 0.6)
                .mana(220, 55)
                .mreg(7.5, 0.65)
                .ad(48, 3.6)
                .as(0.644, 2.9)
                .ar(15, 3.3)
                .mr(30, 0)
                .range(425)
                .ms(335);
        P0000.update(Champion.Varus)
                .health(400, 82)
                .hreg(4.5, 0.55)
                .mana(250, 36)
                .mreg(6.5, 0.5)
                .ad(46, 3)
                .as(0.658, 2.65)
                .ar(13.5, 3.4)
                .mr(30, 0)
                .range(575)
                .ms(335);
        P0000.update(Champion.Vayne)
                .health(359, 83)
                .hreg(4.5, 0.55)
                .mana(173, 27)
                .mreg(6.3, 0.4)
                .ad(50, 3.25)
                .as(0.658, 3.1)
                .ar(9.3, 3.4)
                .mr(30, 0)
                .range(550)
                .ms(330);
        P0000.update(Champion.Veigar)
                .health(355, 82)
                .hreg(4.5, 0.55)
                .mana(250, 55)
                .mreg(6.9, 0.6)
                .ad(48.3, 2.625)
                .as(0.625, 2.24)
                .ar(12.25, 3.75)
                .mr(30, 0)
                .range(525)
                .ms(340);
        P0000.update(Champion.Vi)
                .health(440, 85)
                .hreg(7.5, 0.9)
                .mana(220, 45)
                .mreg(7.0, 0.65)
                .ad(55, 3.5)
                .as(0.643, 2.5)
                .ar(16, 3.5)
                .mr(30, 1.25)
                .range(125)
                .ms(350);
        P0000.update(Champion.Viktor)
                .health(385, 78)
                .hreg(6.75, 0.65)
                .mana(240, 50)
                .mreg(6.9, 0.45)
                .ad(49, 3)
                .as(0.625, 2.11)
                .ar(12, 4)
                .mr(30, 0)
                .range(525)
                .ms(335);
        P0000.update(Champion.Vladimir)
                .health(400, 85)
                .hreg(6, 0.6)
                .ad(45, 3)
                .as(0.6258, 2)
                .ar(12, 3.5)
                .mr(30, 0)
                .range(450)
                .ms(335);
        P0000.update(Champion.Volibear)
                .health(440, 86)
                .hreg(7.0, 0.65)
                .mana(220, 30)
                .mreg(7, 0.65)
                .ad(54, 3.3)
                .as(0.625, 2.9)
                .ar(16.5, 3.5)
                .mr(30, 1.25)
                .range(125)
                .ms(345);
        P0000.update(Champion.Warwick)
                .health(428, 98)
                .hreg(7.05, 0.8)
                .mana(190, 30)
                .mreg(7.1, 0.6)
                .ad(56.76, 3.375)
                .as(0.679, 2.88)
                .ar(16, 3.5)
                .mr(30, 1.25)
                .range(125)
                .ms(345);
        P0000.update(Champion.Wukong)
                .health(435, 85)
                .hreg(5.1, 0.65)
                .mana(202, 38)
                .mreg(6.9, 0.65)
                .ad(54, 3.2)
                .as(0.658, 3)
                .ar(15, 3.5)
                .mr(30, 1.25)
                .range(175)
                .ms(345);
        P0000.update(Champion.Xerath)
                .health(380, 80)
                .hreg(5, 0.55)
                .mana(250, 45)
                .mreg(8, 0.6)
                .ad(52, 3)
                .as(0.625, 1.36)
                .ar(12.6, 3.4)
                .mr(30, 0)
                .range(550)
                .ms(340);
        P0000.update(Champion.XinZhao)
                .health(445, 87)
                .hreg(7, 0.7)
                .mana(213, 31)
                .mreg(6.6, 0.45)
                .ad(52, 3.3)
                .as(0.672, 2.7)
                .ar(16.2, 3.7)
                .mr(30, 1.25)
                .range(175)
                .ms(345);
        P0000.update(Champion.Yorick)
                .health(421, 85)
                .hreg(8.5, 0.7)
                .mana(235, 35)
                .mreg(6.5, 0.45)
                .ad(51.5, 3.5)
                .as(0.625, 3)
                .ar(18, 3.6)
                .mr(30, 1.25)
                .range(125)
                .ms(345);
        P0000.update(Champion.Zed)
                .health(445, 85)
                .hreg(6, 0.65)
                .energy(20)
                .ereg(50)
                .ad(48.6, 3.4)
                .as(0.658, 3.1)
                .ar(17.5, 3.5)
                .mr(30, 1.25)
                .range(125)
                .ms(345);
        P0000.update(Champion.Ziggs)
                .health(390, 80)
                .hreg(5.25, 0.6)
                .mana(250, 50)
                .mreg(6.75, 0.6)
                .ad(54, 3.1)
                .as(0.656, 1.7)
                .ar(12, 3.3)
                .mr(30, 0)
                .range(575)
                .ms(330);
        P0000.update(Champion.Zilean)
                .health(380, 71)
                .hreg(4.6, 0.5)
                .mana(260, 60)
                .mreg(6.95, 0.65)
                .ad(48.6, 3)
                .as(0.625, 2.13)
                .ar(6.75, 3.8)
                .mr(30, 0)
                .range(600)
                .ms(335);
        P0000.update(Champion.Zyra)
                .health(355, 74)
                .hreg(4.85, 0.5)
                .mana(250, 50)
                .mreg(7.1, 0.75)
                .ad(50, 3.2)
                .as(0.625, 1.8)
                .ar(11, 3)
                .mr(30, 0)
                .range(575)
                .ms(325);
    }

    /** The patch. */
    public static Patch P1520 = new Patch(1520, 2012, 12, 03, "Preseason 3", P0000);

    static {
        P1520.update(Item.ShardOfTrueIce);
        P1520.update(Item.LiandrysTorment);
        P1520.update(Item.HauntingGuise);
    }

    /** The patch. */
    public static Patch P1530 = new Patch(1530, 2012, 12, 14, "Preseason Balance Update 1", P1520);

    static {
    }

    /** The patch. */
    public static Patch P1540 = new Patch(1540, 2013, 1, 16, "Preseason Balance Update 2", P1530);

    static {
        P1540.update(Champion.Alistar).ms(330);
    }

    /** The latest patch. */
    public static Patch Latest = P1540;
}
