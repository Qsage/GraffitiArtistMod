package com.qsage.graffitiartistmod.common.item;

import com.qsage.graffitiartistmod.common.registration.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SprayCanItem extends Item { // Должно быть Item, а не BlockItem
    public SprayCanItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos wallPos = context.getClickedPos();
        Direction face = context.getClickedFace();
        BlockPos canvasPos = wallPos.relative(face);

        // Проверяем, что блок зарегистрирован, прежде чем вызывать .get()
        if (!ModBlocks.GRAFFITI_BLOCK.isPresent()) {
            return InteractionResult.FAIL;
        }

        if (level.getBlockState(canvasPos).isAir()) {
            if (!level.isClientSide) {
                // Ставим блок
                level.setBlock(canvasPos, ModBlocks.GRAFFITI_BLOCK.get().defaultBlockState(), 3);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }
}