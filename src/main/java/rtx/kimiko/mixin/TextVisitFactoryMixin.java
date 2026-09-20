package rtx.kimiko.mixin;
import net.minecraft.text.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import rtx.kimiko.api.events.EventBus;
import rtx.kimiko.api.events.impl.render.TextFactoryEvent;

@Mixin(net.minecraft.text.TextVisitFactory.class)

public class TextVisitFactoryMixin {
    @ModifyArg(method="visitFormatted", at=@At(value="INVOKE", target="Lnet/minecraft/text/TextVisitFactory;method_27473(Ljava/lang/String;ILnet/minecraft/class_2583;Lnet/minecraft/class_2583;Lnet/minecraft/class_5224;)Z", ordinal=0), index=0, require = 0)
    private static String kimiko_adjustText(String text) {
        EventBus bus = EventBus.get();
        if (!bus.hasListeners(TextFactoryEvent.class)) {
            return text;
        }
        TextFactoryEvent event = bus.post(new TextFactoryEvent(text));
        return event.getText();
    }
}

