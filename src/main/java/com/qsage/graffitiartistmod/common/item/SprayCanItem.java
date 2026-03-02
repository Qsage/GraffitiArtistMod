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
        BlockPos wallPos = context.getClickedPos(); // Блок, по которому кликнули (напр. камень)
        Direction face = context.getClickedFace();  // Сторона блока
        BlockPos canvasPos = wallPos.relative(face); // Место ПЕРЕД блоком

        // Проверяем, можно ли здесь поставить холст (там должен быть воздух или замещаемый блок)
        BlockState currentState = level.getBlockState(canvasPos);
        if (currentState.isAir()) {
            if (!level.isClientSide) {
                // Устанавливаем наш блок-холст
                level.setBlock(canvasPos, ModBlocks.GRAFFITI_BLOCK.get().defaultBlockState(), 3);

                // Здесь можно добавить звук "пшика" баллончика
                // level.playSound(null, canvasPos, SoundEvents.ASSET_SOUND, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }
}