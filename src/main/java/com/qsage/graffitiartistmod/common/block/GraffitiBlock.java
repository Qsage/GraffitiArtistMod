package com.qsage.graffitiartistmod.common.block;

import com.qsage.graffitiartistmod.common.blockentity.GraffitiBlockEntity;
import com.qsage.graffitiartistmod.common.network.ModMessages;
import com.qsage.graffitiartistmod.common.network.PacketDrawPixel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class GraffitiBlock extends Block implements EntityBlock {
    public GraffitiBlock(Properties props) {
        super(props);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GraffitiBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            // Вычисляем пиксель 0-15
            double fx = hit.getLocation().x - pos.getX();
            double fy = hit.getLocation().y - pos.getY();

            int px = (int)(fx * 16);
            int py = (int)(fy * 16);

            // Отправляем пакет на сервер (нужно создать PacketDrawPixel)
            ModMessages.sendToServer(new PacketDrawPixel(pos, px, py, (byte)1));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}