package com.github.razorplay01.inv_view.container;

import com.github.razorplay01.inv_view.ModTemplate;
import com.github.razorplay01.inv_view.mixin.ServerPlayerAccesor;
import com.github.razorplay01.inv_view.util.CuriosAccess;
import com.github.razorplay01.inv_view.util.InventoryType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

//? >= 1.21.1 {
import net.minecraft.core.component.DataComponents;
//?}

import java.util.ArrayList;
import java.util.List;

/**
 * Manejador del inventario de Curios (normal o cosmético), reescrito desde cero.
 * <p>
 * - Si hay 54 curio slots o menos, se abre un menú genérico con las filas necesarias
 * y todos los slots se muestran de una vez (sin páginas).
 * - Si hay más de 54 (el máximo de {@link MenuType#GENERIC_9x6}), se fuerza un menú
 * de 6 filas y la última fila se reserva SOLO para dos botones de papel de
 * paginación (anterior/siguiente). Las filas superiores muestran 45 curios por
 * página y los cambios se escriben de vuelta en los stack handlers de Curios.
 */
public class PlayerCuriosInventoryScreenHandler extends AbstractInventoryScreenHandler {

	private static final int COLUMNS = 9;
	private static final int SLOT_SIZE = 18;

	/**
	 * Filas máximas que soporta {@link MenuType#GENERIC_9x6}.
	 */
	private static final int MAX_MENU_ROWS = 6;
	/**
	 * Slots máximos de Curios que caben en una sola página sin paginar.
	 */
	private static final int MAX_SINGLE_PAGE_SLOTS = MAX_MENU_ROWS * COLUMNS;
	/**
	 * Filas de contenido visibles por página cuando hay paginación (se reserva la última fila).
	 */
	private static final int CONTENT_ROWS_PER_PAGE = 5;
	/**
	 * Slots de contenido por página en modo paginado.
	 */
	private static final int CONTENT_SLOTS_PER_PAGE = CONTENT_ROWS_PER_PAGE * COLUMNS;

	private static final int PLAYER_INVENTORY_ROWS = 3;
	private static final int HOTBAR_SLOTS = COLUMNS;

	private final boolean cosmetic;
	private final List<CurioSlot> curioSlots;
	private final SimpleContainer content;
	private final SimpleContainer navButtons;
	private final int totalCount;
	private final int contentSlots;
	private final int menuRows;
	private final boolean paged;
	private final int totalPages;

	private int page;
	private int prevButtonSlotIndex = -1;
	private int nextButtonSlotIndex = -1;

	public PlayerCuriosInventoryScreenHandler(int containerId, ServerPlayer viewer, ServerPlayer target,
											  boolean cosmetic, String interactPermission) {
		super(resolveMenuType(countCurioSlots(target, cosmetic)), containerId, viewer, target,
				cosmetic ? InventoryType.CURIOS_COSMETIC : InventoryType.CURIOS,
				resolveContentSlots(countCurioSlots(target, cosmetic)), interactPermission);
		this.cosmetic = cosmetic;
		this.curioSlots = new ArrayList<>();
		this.totalCount = countCurioSlots(target, cosmetic);
		this.paged = this.totalCount > MAX_SINGLE_PAGE_SLOTS;
		this.menuRows = occupiedRows(this.totalCount);
		this.contentSlots = (this.paged ? CONTENT_ROWS_PER_PAGE : this.menuRows) * COLUMNS;
		this.totalPages = this.paged ? (this.totalCount + CONTENT_SLOTS_PER_PAGE - 1) / CONTENT_SLOTS_PER_PAGE : 1;
		this.content = new SimpleContainer(this.contentSlots);
		this.navButtons = new SimpleContainer(2);

		collectCurioSlots();
		loadPageContent();
		addInventorySlots();
		refreshNavButtons();
	}

	// ---------------------------------------------------------------------
	// Inventario virtual (copia por página, los cambios se escribirán de vuelta)
	// ---------------------------------------------------------------------

	private void collectCurioSlots() {
		this.curioSlots.clear();
		CuriosAccess.inventory(this.targetPlayer).ifPresent(handler -> {
			for (ICurioStacksHandler stackHandler : handler.getCurios().values()) {
				int slotCount = this.cosmetic ? stackHandler.getCosmeticStacks().getSlots() : stackHandler.getSlots();
				for (int i = 0; i < slotCount; i++) {
					this.curioSlots.add(new CurioSlot(stackHandler, i, this.cosmetic));
				}
			}
		});
	}

	private int pageBase() {
		return this.paged ? this.page * CONTENT_SLOTS_PER_PAGE : 0;
	}

	private int curioCount() {
		return this.curioSlots.size();
	}

	private ItemStack stackAt(int index) {
		if (index < 0 || index >= curioCount()) {
			return ItemStack.EMPTY;
		}
		ItemStack stack = this.curioSlots.get(index).get();
		return stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
	}

	private void loadPageContent() {
		final int base = pageBase();
		for (int i = 0; i < this.contentSlots; i++) {
			this.content.setItem(i, stackAt(base + i));
		}
	}

	private void saveCuriosContent() {
		final int base = pageBase();
		for (int i = 0; i < this.contentSlots; i++) {
			int slotIndex = base + i;
			if (slotIndex < curioCount()) {
				this.curioSlots.get(slotIndex).set(this.content.getItem(i));
			}
		}
	}

	// ---------------------------------------------------------------------
	// Botones de paginación (solo en modo paginado, última fila)
	// ---------------------------------------------------------------------

	private void refreshNavButtons() {
		if (!this.paged) {
			return;
		}
		this.navButtons.setItem(0, makeNavButton(true));
		this.navButtons.setItem(1, makeNavButton(false));
	}

	private @NotNull ItemStack makeNavButton(boolean previous) {
		ItemStack button = new ItemStack(Items.PAPER);
		int targetPage = previous
				? Math.max(1, this.page)
				: Math.min(this.totalPages, this.page + 2);
		Component label = Component.translatable(
				previous ? "inv_view_neoforge.curios.previous_page" : "inv_view_neoforge.curios.next_page",
				targetPage, this.totalPages);
		//? >= 1.21.1 {
		button.set(DataComponents.CUSTOM_NAME, label);
		//?}
		//? < 1.21.1 {
		/*button.setHoverName(label);
		 *///?}
		return button;
	}

	// ---------------------------------------------------------------------
	// Slot layout
	// ---------------------------------------------------------------------

	@Override
	protected void addInventorySlots() {
		final int contentRows = this.paged ? CONTENT_ROWS_PER_PAGE : this.menuRows;
		// Slots de Curios del jugador objetivo (página actual)
		for (int row = 0; row < contentRows; row++) {
			for (int col = 0; col < COLUMNS; col++) {
				this.addSlot(new Slot(this.content, col + row * COLUMNS, 8 + col * SLOT_SIZE, 18 + row * SLOT_SIZE));
			}
		}

		// Inventario del espectador (3 filas + hotbar)
		final int yOffset = (this.menuRows - 4) * SLOT_SIZE;
		for (int row = 0; row < PLAYER_INVENTORY_ROWS; row++) {
			for (int col = 0; col < COLUMNS; col++) {
				this.addSlot(new Slot(this.viewer.getInventory(), col + row * COLUMNS + COLUMNS,
						8 + col * SLOT_SIZE, 103 + row * SLOT_SIZE + yOffset));
			}
		}
		for (int col = 0; col < HOTBAR_SLOTS; col++) {
			this.addSlot(new Slot(this.viewer.getInventory(), col, 8 + col * SLOT_SIZE, 161 + yOffset));
		}

		// Última fila en modo paginado: únicamente 2 papeles (extremo izquierdo y derecho)
		if (this.paged) {
			final int navY = 18 + (this.menuRows - 1) * SLOT_SIZE;
			this.prevButtonSlotIndex = this.slots.size();
			this.addSlot(new Slot(this.navButtons, 0, 8, navY));
			this.nextButtonSlotIndex = this.slots.size();
			this.addSlot(new Slot(this.navButtons, 1, 8 + (COLUMNS - 1) * SLOT_SIZE, navY));
		}
	}

	// ---------------------------------------------------------------------
	// Interacciones
	// ---------------------------------------------------------------------

	private boolean handleNavigationClick(int slotIndex) {
		if (!this.paged) {
			return false;
		}
		if (slotIndex == this.prevButtonSlotIndex) {
			if (this.page > 0) {
				this.page--;
				changePage();
			}
			return true;
		}
		if (slotIndex == this.nextButtonSlotIndex) {
			if (this.page < this.totalPages - 1) {
				this.page++;
				changePage();
			}
			return true;
		}
		return false;
	}

	private void changePage() {
		loadPageContent();
		refreshNavButtons();
		this.broadcastChanges();
	}

	private void onContentModified() {
		saveCuriosContent();
		if (this.targetPlayer != null) {
			ModTemplate.savePlayerData(((ServerPlayerAccesor) this.viewer).server(), this.targetPlayer);
			this.targetPlayer.inventoryMenu.sendAllDataToRemote();
		}
	}

	//? >= 26 {
	@Override
	public void clicked(int slotIndex, int buttonNum, net.minecraft.world.inventory.ContainerInput containerInput, Player player) {
		if (!canInteract()) {
			return;
		}
		if (handleNavigationClick(slotIndex)) {
			return;
		}
		super.clicked(slotIndex, buttonNum, containerInput, player);
		onContentModified();
	}
	//?}
	//? < 26 {
	/*@Override
	public void clicked(int slotIndex, int button, net.minecraft.world.inventory.ClickType actionType, Player player) {
		if (!canInteract()) {
			return;
		}
		if (handleNavigationClick(slotIndex)) {
			return;
		}
		super.clicked(slotIndex, button, actionType, player);
		onContentModified();
	}
	*///?}

	@Override
	public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
		if (this.paged && (slotIndex == this.prevButtonSlotIndex || slotIndex == this.nextButtonSlotIndex)) {
			return ItemStack.EMPTY;
		}
		ItemStack result = super.quickMoveStack(player, slotIndex);
		onContentModified();
		return result;
	}

	@Override
	public void removed(@NotNull Player player) {
		saveCuriosContent();
		super.removed(player);
	}

	// ---------------------------------------------------------------------
	// Helpers estáticos
	// ---------------------------------------------------------------------

	private static int countCurioSlots(ServerPlayer target, boolean cosmetic) {
		int[] count = {0};
		CuriosAccess.inventory(target).ifPresent(handler -> {
			for (ICurioStacksHandler stackHandler : handler.getCurios().values()) {
				count[0] += cosmetic ? stackHandler.getCosmeticStacks().getSlots() : stackHandler.getSlots();
			}
		});
		return count[0];
	}

	private static int occupiedRows(int totalCount) {
		if (totalCount <= 0) {
			return 1;
		}
		return Math.min(MAX_MENU_ROWS, (totalCount + COLUMNS - 1) / COLUMNS);
	}

	private static MenuType<?> resolveMenuType(int totalCount) {
		int rows = occupiedRows(totalCount);
		return switch (rows) {
			case 1 -> MenuType.GENERIC_9x1;
			case 2 -> MenuType.GENERIC_9x2;
			case 3 -> MenuType.GENERIC_9x3;
			case 4 -> MenuType.GENERIC_9x4;
			case 5 -> MenuType.GENERIC_9x5;
			default -> MenuType.GENERIC_9x6;
		};
	}

	private static int resolveContentSlots(int totalCount) {
		if (totalCount > MAX_SINGLE_PAGE_SLOTS) {
			return CONTENT_SLOTS_PER_PAGE;
		}
		return occupiedRows(totalCount) * COLUMNS;
	}

	// ---------------------------------------------------------------------
	// Referencia directa a un slot de Curios (normal o cosmético)
	// ---------------------------------------------------------------------

	private record CurioSlot(ICurioStacksHandler handler, int slot, boolean cosmetic) {
		private ItemStack get() {
			return this.cosmetic
					? this.handler.getCosmeticStacks().getStackInSlot(this.slot)
					: this.handler.getStacks().getStackInSlot(this.slot);
		}

		private void set(ItemStack stack) {
			if (this.cosmetic) {
				this.handler.getCosmeticStacks().setStackInSlot(this.slot, stack);
			} else {
				this.handler.getStacks().setStackInSlot(this.slot, stack);
			}
		}
	}
}
