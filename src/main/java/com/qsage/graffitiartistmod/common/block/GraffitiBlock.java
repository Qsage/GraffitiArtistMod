package com.qsage.graffitiartistmod.common.block;

import com.qsage.graffitiartistmod.common.blockentity.GraffitiBlockEntity;
import com.qsage.graffitiartistmod.common.registration.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class GraffitiBlock extends Block implements EntityBlock {

    public GraffitiBlock(Properties properties) {
        super(properties);
    }

    // 1. Связываем блок с нашим BlockEntity
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ModBlockEntities.GRAFFITI_BE.get().create(pos, state);
    }

    // 2. Указываем, как рендерить сам КУБ блока
    @Override
    public RenderShape getRenderShape(BlockState state) {
        // MODEL означает, что блок будет использовать обычную JSON-модель (как камень)
        // Если хочешь, чтобы блок был невидимым (только граффити), выбери INVISIBLE
        return RenderShape.MODEL;
    }

    // 3. (Опционально) Если хочешь, чтобы через блок проходил свет
    @Override
    public boolean propagatesSkylightDown(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos) {
        return true;
    }
}