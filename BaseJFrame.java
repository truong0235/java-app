package GUI;

import javax.swing.JFrame;

public class BaseJFrame extends JFrame {
    private String panelString = "";
    private int height = 800;
    private int width = 400;

    public BaseJFrame() {
        super();
        setSize(width, height);
        init();
    }

    // Constructor này quan trọng: Nhóm bạn để Height trước, Width sau
    public BaseJFrame(int height, int width, String panelString){
        super();
        setSize(width, height);
        
        // --- ĐOẠN ĐÃ SỬA ---
        setTitle(panelString); // Hiển thị tiêu đề cửa sổ (QUAN TRỌNG)
        setName(panelString);  // Giữ lại để tương thích logic nhóm
        // -------------------
        
        init();
    }
    
    private void init(){
        // Lưu ý: Nhóm để setVisible(true) ở đây nghĩa là cửa sổ sẽ hiện ngay lập tức
        // nên bên LoginJFrame tôi phải dùng revalidate() để vẽ lại giao diện
        setVisible(true); 
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}