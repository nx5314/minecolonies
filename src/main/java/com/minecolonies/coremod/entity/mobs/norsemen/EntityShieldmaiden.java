package com.minecolonies.coremod.entity.mobs.norsemen;

import com.minecolonies.api.entity.mobs.vikings.AbstractEntityNorsemen;
import com.minecolonies.api.entity.mobs.vikings.IMeleeNorsemenEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/**
 * Class for the Norsemen Shieldmaiden entity.
 */
public class EntityShieldmaiden extends AbstractEntityNorsemen implements IMeleeNorsemenEntity
{

    /**
     * Constructor of the entity.
     *
     * @param worldIn world to construct it in.
     * @param type    the entity type.
     */
    public EntityShieldmaiden(final EntityType<? extends EntityShieldmaiden> type, final Level worldIn)
    {
        super(type, worldIn);
    }
}
