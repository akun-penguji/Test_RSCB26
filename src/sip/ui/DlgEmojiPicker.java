package sip.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

/**
 * Emoji picker untuk DlgChat. Ada 6 kategori: Smileys / People / Medis / Food /
 * Activity / Symbols, ~180 emoji umum. Klik → callback ke caller.
 *
 * <p>Rendering emoji tergantung font OS. Java Swing hanya bisa render glyph
 * monochrome (TrueType outline) — color bitmap emoji (CBDT/COLR) TIDAK support.
 * <ul>
 *   <li>Windows 8+ : {@code Segoe UI Symbol} / {@code Segoe UI Emoji} (built-in, partial outline)</li>
 *   <li>macOS : {@code Symbola} recommended (install via Homebrew)</li>
 *   <li>Linux : butuh {@code fonts-symbola}
 *       ({@code sudo apt install fonts-symbola}) — Noto Color Emoji TIDAK jalan
 *       di Swing karena format CBDT</li>
 * </ul>
 */
public class DlgEmojiPicker extends JDialog {

    public static final Font EMOJI_FONT = pickEmojiFont(14f);

    /**
     * Prefer font emoji monochrome (outline TrueType) — Java 2D tidak bisa render
     * color bitmap emoji (CBDT/COLR format seperti Noto Color Emoji, Apple Color Emoji).
     * Urutan: Symbola → Noto Emoji (mono) → Segoe UI Symbol → Segoe UI Emoji (Windows ada
     * fallback partial) → SansSerif.
     */
    private static Font pickEmojiFont(float size) {
        String[] candidates = {
            "Symbola",
            "Noto Emoji",
            "Segoe UI Symbol",
            "Segoe UI Emoji",
            Font.SANS_SERIF
        };
        Set<String> installed = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String fam : candidates) {
            if (installed.contains(fam)) return new Font(fam, Font.PLAIN, (int) size);
        }
        return new Font(Font.SANS_SERIF, Font.PLAIN, (int) size);
    }

    private static final String[][] GROUPS = {
        {"Smileys", "😀 😃 😄 😁 😆 😅 🤣 😂 🙂 🙃 😉 😊 😇 🥰 😍 🤩 😘 😗 😚 😙 😋 😛 😜 🤪 😝 🤑 🤗 🤭 🤫 🤔 🤐 🤨 😐 😑 😶 😏 😒 🙄 😬 🤥 😌 😔 😪 🤤 😴 😷 🤒 🤕 🤢 🤮 🥵 🥶 😵 🤯 🤠 🥳 😎 🤓 🧐 😕 😟 🙁 😮 😯 😲 😳 🥺 😦 😧 😨 😰 😥 😢 😭 😱 😖 😣 😞 😓 😩 😫 😤 😡 😠 🤬"},
        {"People",  "👋 🤚 🖐 ✋ 🖖 👌 🤌 🤏 ✌ 🤞 🤟 🤘 🤙 👈 👉 👆 🖕 👇 ☝ 👍 👎 ✊ 👊 🤛 🤜 👏 🙌 👐 🤲 🤝 🙏 💪 👀 👁 👂 👃 👅 👄 💋 💯 💢 💥 💫 💦 💨 🕳 💣"},
        {"Medis",   "🏥 🚑 💊 💉 🩹 🩺 🩻 🧬 🦠 🧪 🧫 🧴 🧻 🧼 🧽 🪥 🪒 🧷 🔬 🔭 ❤ 💔 💓 💘 💕 💖 💗 🩸 👶 👧 👦 👩 👨 👵 👴 🧓"},
        {"Food",    "🍎 🍊 🍋 🍌 🍉 🍇 🍓 🫐 🍈 🍒 🍑 🥭 🍍 🥥 🥝 🍅 🍆 🥑 🥦 🥬 🥒 🌶 🫒 🌽 🥕 🧄 🧅 🥔 🍠 🥐 🥯 🍞 🥖 🥨 🧀 🥚 🍳 🧈 🥞 🧇 🥓 🥩 🍗 🍖 🦴 🌭 🍔 🍟 🍕 🥪 🥙 🧆 🌮 🌯 🫔 🥗 🥘 🫕 🥫 🍝 🍜 🍲 🍛 🍣 🍱 🥟 🦪 🍤 🍙 🍚 🍘 🍥 🥠 🥮 🍢 🍡 🍧 🍨 🍦 🥧 🧁 🍰 🎂 🍮 🍭 🍬 🍫 🍿 🍩 🍪 🌰 🥜 🍯 🥛 🍼 ☕ 🍵 🧃 🥤 🧋 🍶 🍺 🍻 🥂 🍷 🥃 🍸 🍹 🧉 🍾"},
        {"Activity","⚕ ❤ 🚨 🔔 ⏰ 🕐 🕑 🕒 🕓 🕔 🕕 🕖 🕗 🕘 🕙 🕚 🕛 📞 ☎ 📱 📲 💻 🖥 🖨 ⌨ 🖱 📡 🔋 🔌 💡 🔦 🕯 🧯 🛢 💸 💵 💴 💶 💷 💰 💳 🧾 💎"},
        {"Symbol",  "✅ ❌ ⭕ 🛑 ⚠ 🚫 ✔ ❓ ❔ ❕ ❗ ‼ ⁉ 💯 🔴 🟠 🟡 🟢 🔵 🟣 ⚫ ⚪ 🟤 ⬛ ⬜ ◼ ◻ ◾ ◽ ▪ ▫ 🔶 🔷 🔸 🔹 🔺 🔻 💠 🔘 🔳 🔲 ➕ ➖ ➗ ✖ 💲 💱 ™ ©"}
    };

    public static void show(Window parent, Consumer<String> onPick) {
        new DlgEmojiPicker(parent, onPick).setVisible(true);
    }

    private DlgEmojiPicker(Window parent, Consumer<String> onPick) {
        super(parent, "Emoji", ModalityType.APPLICATION_MODAL);
        setSize(new Dimension(520, 360));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(tabs.getFont().deriveFont(11f));
        for (String[] grp : GROUPS) {
            tabs.addTab(grp[0], buildGrid(grp[1].split("\\s+"), onPick));
        }
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(tabs, BorderLayout.CENTER);
    }

    private JScrollPane buildGrid(String[] emojis, Consumer<String> onPick) {
        JPanel grid = new JPanel(new GridLayout(0, 10, 2, 2));
        grid.setBorder(new EmptyBorder(6, 6, 6, 6));
        grid.setBackground(Color.WHITE);
        for (String e : emojis) {
            if (e == null || e.isEmpty()) continue;
            JButton b = new JButton(e);
            b.setFont(EMOJI_FONT.deriveFont(18f));
            b.setPreferredSize(new Dimension(38, 38));
            b.setFocusPainted(false);
            b.setMargin(new Insets(0, 0, 0, 0));
            b.setBackground(Color.WHITE);
            b.setBorder(BorderFactory.createLineBorder(new Color(0xE5E7EB), 1));
            b.addActionListener(ev -> {
                onPick.accept(e);
                dispose();
            });
            grid.add(b);
        }
        JScrollPane sp = new JScrollPane(grid);
        sp.setBorder(null);
        return sp;
    }
}
