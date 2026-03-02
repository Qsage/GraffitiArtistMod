package com.qsage.graffitiartistmod.common.block;

import com.qsage.graffitiartistmod.common.blockentity.GraffitiBlockEntity;
import com.qsage.graffitiartistmod.common.item.SprayCanItem;
import com.qsage.graffitiartistmod.common.network.ModMessages;
import com.qsage.graffitiartistmod.common.network.packet.PacketDrawPixel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GraffitiBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    // Хитбоксы, которые теперь будут РЕАЛЬНО меняться
    protected static final VoxelShape NORTH_AABB = Block.box(0, 0, 15, 16, 16, 16);
    protected static final VoxelShape SOUTH_AABB = Block.box(0, 0, 0, 16, 16, 1);
    protected static final VoxelShape WEST_AABB  = Block.box(15, 0, 0, 16, 16, 16);
    protected static final VoxelShape EAST_AABB  = Block.box(0, 0, 0, 1, 16, 16);
    protected static final VoxelShape UP_AABB    = Block.box(0, 0, 0, 16, 1, 16);
    protected static final VoxelShape DOWN_AABB  = Block.box(0, 15, 0, 16, 16, 16);

    public GraffitiBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        // 1. Проверяем, что в руке именно баллончик
        if (player.getItemInHand(hand).getItem() instanceof SprayCanItem) {
            if (level.isClientSide) {
                // ЛОГИКА РИСОВАНИЯ (Клиентская часть)
                Vec3 localHit = hit.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
                Direction facing = state.getValue(FACING);

                int px = 0;
                int py = (int)((1.0 - localHit.y) * 16);

                // Математика должна соответствовать хитбоксу
                switch (facing) {
                    case NORTH -> px = (int)((1.0 - localHit.x) * 16);
                    case SOUTH -> px = (int)(localHit.x * 16);
                    case WEST  -> px = (int)(localHit.z * 16);
                    case EAST  -> px = (int)((1.0 - localHit.z) * 16);
                }

                px = Math.max(0, Math.min(15, px));
                py = Math.max(0, Math.min(15, py));

                ModMessages.sendToServer(new PacketDrawPixel(pos, px, py, (byte)1));
            }
            // ЭТО ГЛАВНОЕ: возвращаем SUCCESS, чтобы игра НЕ ставила блок сверху!
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }



    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GraffitiBlockEntity(pos, state);
    }

}