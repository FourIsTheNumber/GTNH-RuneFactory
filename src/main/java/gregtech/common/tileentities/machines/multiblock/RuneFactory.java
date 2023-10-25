package gregtech.common.tileentities.machines.multiblock;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.enums.Mods.*;
import static gregtech.api.multitileentity.multiblock.base.MultiBlockPart.ITEM_IN;
import static gregtech.api.multitileentity.multiblock.base.MultiBlockPart.ITEM_OUT;
import static gregtech.api.util.GT_StructureUtilityMuTE.ofMuTECasings;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.util.Vec3Impl;
import com.gtnewhorizons.modularui.api.math.Alignment;
import com.gtnewhorizons.modularui.api.math.Color;
import com.gtnewhorizons.modularui.api.widget.IWidgetBuilder;
import com.gtnewhorizons.modularui.common.widget.ButtonWidget;
import com.gtnewhorizons.modularui.common.widget.MultiChildWidget;
import com.gtnewhorizons.modularui.common.widget.textfield.TextFieldWidget;

import gregtech.api.enums.GT_Values;
import gregtech.api.gui.modularui.GT_UITextures;
import gregtech.api.multitileentity.enums.GT_MultiTileCasing;
import gregtech.api.multitileentity.multiblock.base.ComplexParallelController;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.common.tileentities.machines.multiblock.logic.RuneFactoryProcessingLogic;

public class RuneFactory extends ComplexParallelController<RuneFactory, RuneFactoryProcessingLogic> {

    private static IStructureDefinition<RuneFactory> STRUCTURE_DEFINITION = null;
    protected static final String STRUCTURE_MAIN = "Main";
    protected static final String STRUCTURE_PIECE_T1 = "T1";
    protected static final String STRUCTURE_PIECE_T2 = "T2";
    private static final Vec3Impl STRUCTURE_OFFSET_T1 = new Vec3Impl(1, 1, 0);
    private static final Vec3Impl STRUCTURE_OFFSET_T2 = new Vec3Impl(11, 3, 1);
    protected static final int MAX_PROCESSES = 2;
    protected static final int PROCESS_WINDOW_BASE_ID = 100;

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("Rune Factory")
            .addInfo("Makes runes better!")
            .beginStructureBlock(3, 3, 3, true)
            .addCasingInfoExactly("Botanic Machine Casings", 25, false)
            .toolTipFinisher(GT_Values.AuthorFourIsTheNumber);
        return tt;
    }

    @Override
    public IStructureDefinition<RuneFactory> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<RuneFactory>builder()
                .addShape(
                    STRUCTURE_PIECE_T1,
                    transpose(
                        // spotless:off
                    	new String[][] { { "AAA", "A~A", "AAA" }, { "AAA", "A-A", "AAA" }, { "AAA", "AAA", "AAA" } }))
					.addShape(
						STRUCTURE_PIECE_T2,
						transpose(
						new String[][] {
							{" AAAAA ","AAAAAAA","AAAAAAA","AAAAAAA","AAAAAAA","AAAAAAA"," AAAAA "},
						    {" ABBBA ","AA   AA","B     B","B     B","B     B","AA   AA"," ABBBA "},
						    {" ABBBA ","AA   AA","B     B","B     B","B     B","AA   AA"," ABBBA "},
						    {" ABBBA ","AA   AA","B     B","B  C  B","B     B","AA   AA"," ABBBA "},
						    {" AAAAA ","AAAAAAA","AAAAAAA","AAAAAAA","AAAAAAA","AAAAAAA"," AAAAA "}
						}))
						//spotless:on
                .addElement('A', ofMuTECasings(ITEM_IN | ITEM_OUT, GT_MultiTileCasing.BotanicCasing.getCasing()))
                .addElement('B', ofMuTECasings(ITEM_IN | ITEM_OUT, GT_MultiTileCasing.BotanicCasing.getCasing()))
                .addElement('C', ofMuTECasings(ITEM_IN | ITEM_OUT, GT_MultiTileCasing.BotanicCasing.getCasing()))
                // .addElement('B', ofBlockUnlocalizedName(Botania.ID, "manaGlass", 0, true))
                // .addElement('C', ofBlockUnlocalizedName(Botania.ID, "runeAltar", 0, true))
                .build();
            buildState.stopBuilding();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    protected MultiChildWidget createMainPage(IWidgetBuilder<?> builder) {
        MultiChildWidget child = super.createMainPage(builder);
        for (int i = 0; i < MAX_PROCESSES; i++) {
            final int processIndex = i;
            child.addChild(
                new ButtonWidget().setPlayClickSound(true)
                    .setOnClick(
                        (clickData, widget) -> {
                            if (!widget.isClient()) widget.getContext()
                                .openSyncedWindow(PROCESS_WINDOW_BASE_ID + processIndex);
                        })
                    .setBackground(GT_UITextures.BUTTON_STANDARD, GT_UITextures.OVERLAY_BUTTON_WHITELIST)
                    .setSize(18, 18)
                    .setEnabled((widget -> processIndex < maxComplexParallels))
                    .setPos(20 * (i % 4) + 18, 18 + (i / 4) * 20));
        }
        child.addChild(
            new TextFieldWidget().setGetterInt(() -> maxComplexParallels)
                .setSetterInt(parallel -> setMaxComplexParallels(parallel, true))
                .setNumbers(1, MAX_PROCESSES)
                .setTextColor(Color.WHITE.normal)
                .setTextAlignment(Alignment.Center)
                .addTooltip("Tier")
                .setBackground(GT_UITextures.BACKGROUND_TEXT_FIELD)
                .setSize(18, 18)
                .setPos(130, 85));
        return child;
    }

    @Override
    public void readMultiTileNBT(NBTTagCompound nbt) {
        super.readMultiTileNBT(nbt);
        setMaxComplexParallels(nbt.getInteger("processors"), false);
    }

    @Override
    public void writeMultiTileNBT(NBTTagCompound nbt) {
        super.writeMultiTileNBT(nbt);
        nbt.setInteger("processors", maxComplexParallels);
    }

    @Override
    public void construct(ItemStack trigger, boolean hintsOnly) {
        buildState.startBuilding(getStartingStructureOffset());
        buildPiece(STRUCTURE_PIECE_T1, trigger, hintsOnly, buildState.stopBuilding());
        if (maxComplexParallels > 1) {
            buildState.addOffset(STRUCTURE_OFFSET_T2);
            buildPiece(STRUCTURE_PIECE_T2, trigger, hintsOnly, buildState.getCurrentOffset());
        }
        buildState.stopBuilding();
    }

    @Override
    public boolean checkMachine() {
        buildState.startBuilding(getStartingStructureOffset());
        if (!checkPiece(STRUCTURE_PIECE_T1, buildState.getCurrentOffset())) return buildState.failBuilding();
        if (maxComplexParallels > 1) {
            buildState.addOffset(STRUCTURE_OFFSET_T2);
            if (!checkPiece(STRUCTURE_PIECE_T2, buildState.getCurrentOffset())) return buildState.failBuilding();
        }
        buildState.stopBuilding();
        return super.checkMachine();
    }

    @Override
    public short getCasingRegistryID() {
        return 11;
    }

    @Override
    public String getTileEntityName() {
        return "gt.multitileentity.multiblock.runefactory";
    }

    @Override
    public int getCasingMeta() {
        return GT_MultiTileCasing.BotanicCasing.getId();
    }

    @Override
    public Vec3Impl getStartingStructureOffset() {
        return STRUCTURE_OFFSET_T1;
    }

    @Override
    protected boolean hasFluidInput() {
        return false;
    }

    @Override
    public String getLocalName() {
        return StatCollector.translateToLocal("gt.multiBlock.controller.runeFactory");
    }

    @Override
    @Nonnull
    protected RuneFactoryProcessingLogic createProcessingLogic() {
        return new RuneFactoryProcessingLogic();
    }
}
