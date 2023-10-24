package gregtech.common.tileentities.machines.multiblock;

import static gregtech.api.multitileentity.multiblock.base.MultiBlockPart.ITEM_IN;
import static gregtech.api.multitileentity.multiblock.base.MultiBlockPart.ITEM_OUT;
import static gregtech.api.util.GT_StructureUtilityMuTE.ofMuTECasings;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.ISurvivalBuildEnvironment;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import com.gtnewhorizon.structurelib.util.Vec3Impl;

import gregtech.api.enums.GT_Values;
import gregtech.api.multitileentity.enums.GT_MultiTileCasing;
import gregtech.api.multitileentity.multiblock.base.Controller;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.common.tileentities.machines.multiblock.logic.RuneFactoryProcessingLogic;

public class RuneFactory extends Controller<RuneFactory, RuneFactoryProcessingLogic> {

    private static IStructureDefinition<RuneFactory> STRUCTURE_DEFINITION = null;
    protected static final String MAIN = "Main";
    private static final Vec3Impl OFFSET = new Vec3Impl(1, 1, 0);

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("Rune Factory")
            .addInfo("Makes runes better!")
            .beginStructureBlock(3, 3, 3, true)
            .addCasingInfoExactly("Botanic Machine Casings", 25, false)
            .toolTipFinisher(GT_Values.AuthorBlueWeabo);
        return tt;
    }

    @Override
    public IStructureDefinition<RuneFactory> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<RuneFactory>builder()
                .addShape(
                    MAIN,
                    new String[][] { { "AAA", "A~A", "AAA" }, { "AAA", "A-A", "AAA" }, { "AAA", "AAA", "AAA" } })
                .addElement('A', ofMuTECasings(ITEM_IN | ITEM_OUT, GT_MultiTileCasing.BotanicCasing.getCasing()))
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    public void construct(ItemStack trigger, boolean hintsOnly) {
        buildState.startBuilding(getStartingStructureOffset());
        buildPiece(MAIN, trigger, hintsOnly, buildState.stopBuilding());
    }

    @Override
    public boolean checkMachine() {
        buildState.startBuilding(getStartingStructureOffset());
        return checkPiece(MAIN, buildState.stopBuilding());
    }

    @Override
    public int survivalConstruct(ItemStack trigger, int elementBudget, ISurvivalBuildEnvironment env) {
        buildState.startBuilding(getStartingStructureOffset());
        return survivalBuildPiece(MAIN, trigger, buildState.stopBuilding(), elementBudget, env, false);
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
        return OFFSET;
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
