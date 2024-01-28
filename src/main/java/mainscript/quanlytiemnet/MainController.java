package mainscript.quanlytiemnet;

import DAL.TaiKhoanDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import BLL.InFoMayTinh.DanhSachMT;
import BLL.InFoTaiKhoan.DanhSachTK;
import BLL.InFoTaiKhoan.TaiKhoan;
import BLL.InFoThongTinSD.DSThongTinSD;
import BLL.InFoThongTinSD.ThongTinSuDung;
import BLL.MainControllerStatusManagement;
import DAL.ThongTinSuDungDAO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainController implements Initializable {

    public static boolean checkingTimeline = true;

    public static List<ThongTinSuDung> StackingOnl = new ArrayList<>();

    @FXML
    private StackPane MainPane;

    @FXML
    private BorderPane MainSwitching;

    @FXML
    private Label SoMayBaoTri;

    @FXML
    private Label SoMayCoSan;

    @FXML
    private Label SoMayHong;

    @FXML
    private Label SoMayOnl;

    @FXML
    private AnchorPane StatusPane;

    @FXML
    private Label TenAdmin;

    @FXML
    private Label TongThoiGianHomNay;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    public void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {
                LocalDateTime now = LocalDateTime.now();
                Iterator<ThongTinSuDung> iterator = StackingOnl.iterator();

                for(ThongTinSuDung ttsd : new DSThongTinSD().LayCacMayDagSDTrongTTSD()){
                    if(ttsd.getTgKetThuc().format(formatter).equals(now.format(formatter))){
                        try {
                            TaiKhoan tk = new DanhSachTK().TimTKTraVeTK(ttsd.getUsername(), ttsd.getSdt());
                            tk.setDangSD(false);
                            new DanhSachTK().CapNhatTaiKhoan(tk);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }

                    }
                }

                while (iterator.hasNext()) {
                    ThongTinSuDung ttsdStack = iterator.next();
                    for (ThongTinSuDung ttsd : new ThongTinSuDungDAO().getAll()) {
                        if (ttsd.getTgBatDau().format(formatter).equals(ttsdStack.getTgBatDau().format(formatter)) && !(ttsd.getDagSD())) {

                            iterator.remove();
                            ChuyenCanhFXML object = new ChuyenCanhFXML();
                            Pane view = object.getPage("/TrangThaiMayTinh/MainTrangThaiMT.fxml");
                            MainSwitching.setCenter(view);
                            CapNhatMainStatus();

                            Platform.runLater(() -> {
                                if(checkingTimeline){
                                    showAlert("Thông báo", "Đã ngắt kết nối máy " + ttsdStack.getMaMay() + " do sđt " + ttsdStack.getSdt() + " đã hết tiền", Alert.AlertType.INFORMATION);
                                }
                                checkingTimeline = true;
                                XuLyTKAmTien();
                            });
                        }
                    }
                }
            })
    );

    public void XuLyTKAmTien(){
        try {
            for(TaiKhoan tk : new TaiKhoanDAO().getAll()){
                if(tk.getSoTienConLai() < 0){
                    tk.setSoTienConLai(0);
                    new DanhSachTK().CapNhatTaiKhoan(tk);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        StackingOnl = new DSThongTinSD().LayCacMayDagSDTrongTTSD();
        TenAdmin.setText(new DanhSachTK().getTaiKhoanDangNhap().getUsername());
        XuLyTKAmTien();
        CapNhatMainStatus();
        MainControllerStatusManagement.setMainController(this);
        ChuyenCanhFXML object = new ChuyenCanhFXML();
        Pane view = object.getPage("/DanhSachTK/MainDSTK.fxml");
        MainSwitching.setCenter(view);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void CapNhatMainStatus(){
        SoMayOnl.setText(String.valueOf(new DanhSachMT().MayDangONL()));
        SoMayCoSan.setText(String.valueOf(new DanhSachMT().MayCoSan()));
        SoMayHong.setText(String.valueOf(new DanhSachMT().MayBiHong()));
        SoMayBaoTri.setText(String.valueOf(new DanhSachMT().MayBaoTri()));
        TongThoiGianHomNay.setText(new DSThongTinSD().TongGioChoiHomNay());
    }
    @FXML
    public void ChonDSKH() {
        CapNhatMainStatus();
        ChuyenCanhFXML object = new ChuyenCanhFXML();
        Pane view = object.getPage("/DanhSachTK/MainDSTK.fxml");
        MainSwitching.setCenter(view);
    }

    @FXML
    public void ChonTK() {
        CapNhatMainStatus();
        ChuyenCanhFXML object = new ChuyenCanhFXML();
        Pane view = object.getPage("/ThongKe/MainTK.fxml");
        MainSwitching.setCenter(view);
    }

    @FXML
    public void ChonTrangThaiMT() {
        CapNhatMainStatus();
        ChuyenCanhFXML object = new ChuyenCanhFXML();
        Pane view = object.getPage("/TrangThaiMayTinh/MainTrangThaiMT.fxml");
        MainSwitching.setCenter(view);
    }

    @FXML
    public void ChonDSMT() {
        CapNhatMainStatus();
        ChuyenCanhFXML object = new ChuyenCanhFXML();
        Pane view = object.getPage("/DSMayTinh/MainDSMT.fxml");
        MainSwitching.setCenter(view);
    }
    public void ChonKHTM(){
        CapNhatMainStatus();
        ChuyenCanhFXML object = new ChuyenCanhFXML();
        Pane view = object.getPage("/KHTiemNang/MainKHTM.fxml");
        MainSwitching.setCenter(view);
    }

    public void ChonDangXuat(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận đăng xuất");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc chắn muốn đăng xuất?");

        // Lấy kết quả người dùng chọn
        Optional<ButtonType> result = alert.showAndWait();

        // Xử lý kết quả
        if (result.orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // Nếu người dùng chọn "Có", thực hiện đăng xuất và chuyển đến trang login.fxml

            // Close the current stage (MainController's stage)
            Stage currentStage = (Stage) MainPane.getScene().getWindow();
            currentStage.close();

            try {
                // Open the login stage
                FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("login.fxml"));
                Parent loginRoot = loginLoader.load();
                Stage loginStage = new Stage();
                loginStage.setTitle("Quản lý Net");
                loginStage.setScene(new Scene(loginRoot, 800, 535));
                loginStage.setResizable(false);
                loginStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
