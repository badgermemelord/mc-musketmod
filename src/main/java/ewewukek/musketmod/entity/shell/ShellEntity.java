package ewewukek.musketmod.entity.shell;

import ewewukek.musketmod.MusketMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.*;

import java.util.Optional;

public class ShellEntity extends AbstractHurtingProjectile {
    public static final EntityDataAccessor<Float> INITIAL_SPEED = SynchedEntityData.defineId(ShellEntity.class, EntityDataSerializers.FLOAT);

    public static final double MIN_DAMAGE = 0.5;
    public static final double GRAVITY = 0.05;
    public static final double AIR_FRICTION = 0.99;
    public static final double WATER_FRICTION = 0.6;
    public static final short LIFETIME = 200;

    public static double maxDistance;

    public float damageMultiplier;
    public boolean ignoreInvulnerableTime;
    public float distanceTravelled;
    public short tickCounter;

    public ShellEntity(EntityType<ShellEntity>entityType, Level world) {
        super(entityType, world);
    }

    public ShellEntity(Level world) {
        this(MusketMod.SHELL_ENTITY_TYPE, world);
    }

    public boolean isFirstTick() {
        return tickCounter == 0;
    }

    public DamageSource causeMusketDamage(ShellEntity shell, Entity attacker) {
        return (new IndirectEntityDamageSource("musket", shell, attacker)).setProjectile();
    }

    public void discardOnNextTick() {
        tickCounter = LIFETIME;
    }

    @Override
    public void tick() {
        LivingEntity attacker = null;
        if (++tickCounter >= LIFETIME || distanceTravelled > maxDistance) {
            discard();
            return;
        }

        Vec3 motion = getDeltaMovement();
        Vec3 from = position();
        Vec3 to = from.add(motion);

        Vec3 waterPos = from;
        wasTouchingWater = updateFluidHeightAndDoFluidPushing(FluidTags.WATER, 0);
        if (wasTouchingWater) {
            motion = motion.scale(WATER_FRICTION);
            to = from.add(motion);
            setDeltaMovement(motion);
        }

        HitResult hitResult = level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

        // prevents hitting entities behind an obstacle
        if (hitResult.getType() != HitResult.Type.MISS) {
            to = hitResult.getLocation();
        }

        EntityHitResult entityHitResult = findHitEntity(from, to);
        if (entityHitResult != null) {
            hitResult = entityHitResult;
            to = hitResult.getLocation();
        }

        if (!wasTouchingWater) {
            BlockHitResult fluidHitResult = level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, this));
            if (fluidHitResult.getType() == HitResult.Type.BLOCK) {
                FluidState fluid = level.getFluidState(fluidHitResult.getBlockPos());
                double distanceToFluid = fluidHitResult.getLocation().subtract(from).length();
                double distanceToHit = to.subtract(from).length();

                if (fluid.is(FluidTags.WATER)) {
                    wasTouchingWater = true;
                    waterPos = fluidHitResult.getLocation();
                    double velocity = motion.length();
                    double timeInWater = 1 - distanceToFluid / velocity;
                    double newVelocity = velocity * (1 - timeInWater + timeInWater * Math.pow(WATER_FRICTION, timeInWater));

                    if (hitResult.getType() != HitResult.Type.MISS) {
                        if (distanceToFluid < distanceToHit) {
                            if (distanceToHit < newVelocity) {
                                timeInWater = (distanceToHit - distanceToFluid) / velocity;
                                newVelocity = velocity * (1 - timeInWater + timeInWater * Math.pow(WATER_FRICTION, timeInWater));
                            } else {
                                hitResult = BlockHitResult.miss(null, null, null);
                            }
                        } else {
                            fluidHitResult = BlockHitResult.miss(null, null, null);
                        }
                    }
                    motion = motion.scale(newVelocity / velocity);
                    to = from.add(motion);
                    setDeltaMovement(motion);

                    if (fluidHitResult.getType() != HitResult.Type.MISS) {
                        int impactParticleCount = (int)(getDeltaMovement().lengthSqr() / 10);
                        if (impactParticleCount > 0) {
                            Vec3 pos = fluidHitResult.getLocation();
                            double yv = fluidHitResult.getDirection() == Direction.UP ? 0.02 : 0;
                            for (int i = 0; i < impactParticleCount; ++i) {
                                level.addParticle(
                                    ParticleTypes.SPLASH,
                                    pos.x, pos.y, pos.z,
                                    random.nextGaussian() * 0.01,
                                    random.nextGaussian() * 0.01 + yv,
                                    random.nextGaussian() * 0.01
                                );
                            }
                        }
                    }
                } else if (fluid.is(FluidTags.LAVA)) {
                    if (hitResult.getType() == HitResult.Type.MISS || distanceToFluid < distanceToHit) {
                        hitResult = fluidHitResult;
                        to = fluidHitResult.getLocation();
                    }
                }
            }
        }

        if (hitResult.getType() != HitResult.Type.MISS) {
            if (!level.isClientSide) {
                onHit(hitResult);
                discardOnNextTick();
                Vec3 pos = hitResult.getLocation();
                level.explode(attacker, pos.x, pos.y, pos.z, 4.0F, Explosion.BlockInteraction.DESTROY);

            } else if (hitResult.getType() == HitResult.Type.BLOCK) {
                int impactParticleCount = (int)(getDeltaMovement().lengthSqr() / 20);
                if (impactParticleCount > 0) {
                    BlockState blockstate = level.getBlockState(((BlockHitResult)hitResult).getBlockPos());
                    BlockParticleOption particleOption = new BlockParticleOption(ParticleTypes.BLOCK, blockstate);
                    Vec3 pos = hitResult.getLocation();
                    for (int i = 0; i < impactParticleCount; ++i) {
                        level.addParticle(
                            particleOption,
                            pos.x, pos.y, pos.z,
                            random.nextGaussian() * 0.01,
                            random.nextGaussian() * 0.01,
                            random.nextGaussian() * 0.01
                        );
                    }
                    //put explosion code here
                    int posX = (int)pos.x;
                    int posY = (int)pos.y;
                    int posZ = (int)pos.z;
                    BlockPos BP = new BlockPos(posX, posY, posZ);
                    level.setBlock(BP, Blocks.GOLD_BLOCK.defaultBlockState(), 1,1);
                }
                discard();
            }
        }

        if (wasTouchingWater) {
            double len = motion.length();
            Vec3 step = motion.scale(1 / len);
            Vec3 pos = waterPos.add(step.scale(0.5));
            while (len > 0.5) {
                pos = pos.add(step);
                len -= 1;
                level.addParticle(ParticleTypes.BUBBLE, pos.x, pos.y, pos.z, 0, 0, 0);
            }
        } else {
            motion = motion.scale(AIR_FRICTION);
        }

        setDeltaMovement(motion.subtract(0, GRAVITY, 0));
        setPos(to);
        distanceTravelled += to.subtract(from).length();
        checkInsideBlocks();
    }

    @Override
    public void onHitEntity(EntityHitResult hitResult) {
        Entity target = hitResult.getEntity();
        if (target instanceof Player) {
            Entity shooter = getOwner();
            if (shooter instanceof Player && !((Player)shooter).canHarmPlayer((Player)target)) {
                target = null;
            }
        }
        if (target != null) {
            Entity shooter = getOwner();
            DamageSource damagesource = causeMusketDamage(this, shooter != null ? shooter : this);

            float damage = damageMultiplier * (float)getDeltaMovement().lengthSqr();
            if (damage > MIN_DAMAGE) {
                int oldInvulnerableTime = target.invulnerableTime;
                if (ignoreInvulnerableTime) target.invulnerableTime = 0;
                boolean beenHurt = target.hurt(damagesource, damage);
                if (ignoreInvulnerableTime && !beenHurt) target.invulnerableTime = oldInvulnerableTime;
            }
        }
    }

    public EntityHitResult findHitEntity(Vec3 start, Vec3 end) {
        Vec3 motion = getDeltaMovement();

        Entity resultEntity = null;
        Vec3 resultVec = null;
        double resultDist = 0;

        AABB aabbSelection = getBoundingBox().expandTowards(motion).inflate(0.5);
        for (Entity entity : level.getEntities(this, aabbSelection, this::canHitEntity)) {
            AABB aabb = entity.getBoundingBox();
            Optional<Vec3> optional = aabb.clip(start, end);
            if (!optional.isPresent()) {
                aabb = aabb.move( // previous tick position
                    entity.xOld - entity.getX(),
                    entity.yOld - entity.getY(),
                    entity.zOld - entity.getZ()
                );
                optional = aabb.clip(start, end);
            }
            if (optional.isPresent()) {
                double dist = start.distanceToSqr(optional.get());
                if (dist < resultDist || resultEntity == null) {
                    resultEntity = entity;
                    resultVec = optional.get();
                    resultDist = dist;
                }
            }
        }

        return resultEntity != null ? new EntityHitResult(resultEntity, resultVec) : null;
    }

    public void setInitialSpeed(float speed) {
        entityData.set(INITIAL_SPEED, speed);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(INITIAL_SPEED, (float)0);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        damageMultiplier = compound.getFloat("damageMultiplier");
        ignoreInvulnerableTime = compound.getByte("ignoreInvulnerableTime") != 0;
        distanceTravelled = compound.getFloat("distanceTravelled");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("damageMultiplier", damageMultiplier);
        compound.putByte("ignoreInvulnerableTime", (byte)(ignoreInvulnerableTime ? 1 : 0));
        compound.putFloat("distanceTravelled", distanceTravelled);
    }

    // workaround for ClientboundAddEntityPacket.LIMIT
    @Override
    public Packet<?> getAddEntityPacket() {
        Entity owner = getOwner();
        return new ClientboundAddEntityPacket(
            getId(), getUUID(),
            getX(), getY(), getZ(),
            getXRot(), getYRot(),
            getType(), owner != null ? owner.getId() : 0,
            getDeltaMovement().scale(ClientboundAddEntityPacket.LIMIT / entityData.get(INITIAL_SPEED))
        );
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        Vec3 packet_velocity = new Vec3(packet.getXa(), packet.getYa(), packet.getZa());
        setDeltaMovement(packet_velocity.scale(1.0 / ClientboundAddEntityPacket.LIMIT));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (INITIAL_SPEED.equals(accessor) && level.isClientSide) {
            setDeltaMovement(getDeltaMovement().scale(entityData.get(INITIAL_SPEED)));
        }
    }
}