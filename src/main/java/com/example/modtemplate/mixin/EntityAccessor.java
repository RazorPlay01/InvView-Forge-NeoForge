package com.example.modtemplate.mixin;
//? >= 1.21.1{

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {
	@Invoker("setLevel")
	void callSetWorld(Level world);
}
//?}
