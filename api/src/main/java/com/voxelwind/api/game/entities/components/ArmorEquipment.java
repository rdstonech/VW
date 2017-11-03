package com.voxelwind.api.game.entities.components;

import com.voxelwind.api.game.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Specifies the armor that an {@link com.voxelwind.api.game.entities.Entity} wears. This is a {@link Component} that an entity can provide.
 */
public interface ArmorEquipment extends Component {
    /**
     * Returns the helmet the player is wearing.
     * @return the helmet worn
     */
    Optional<ItemStack> getHelmet();

    /**
     * Sets the helmet the player is wearing.
     * @param stack the helmet to wear
     */
    void setHelmet(@Nullable ItemStack stack);

    /**
     * Returns the chestplate the player is wearing.
     * @return the chestplate worn
     */
    Optional<ItemStack> getChestplate();

    /**
     * Sets the chestplate the player is wearing.
     * @param stack the chestplate to wear
     */
    void setChestplate(@Nullable ItemStack stack);

    /**
     * Returns the leggings the player is wearing.
     * @return the leggings worn
     */
    Optional<ItemStack> getLeggings();

    /**
     * Sets the leggings the player is wearing.
     * @param stack the leggings to wear
     */
    void setLeggings(@Nullable ItemStack stack);

    /**
     * Returns the boots the player is wearing.
     * @return the boots worn
     */
    Optional<ItemStack> getBoots();

    /**
     * Sets the boots the player is wearing.
     * @param stack the boots to wear
     */
    void setBoots(@Nullable ItemStack stack);
}
