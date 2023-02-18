package san.kuroinu.lock_barrel_plugin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.sql.*;

public class Listeners implements Listener {

    public Listeners() throws SQLException {
    }

    @EventHandler
    public void onBarrelClick(PlayerInteractEvent event) throws SQLException {
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType().toString().equals("BARREL") && !event.getPlayer().isOp() && event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
            event.getPlayer().sendMessage("onBarrelClick");
            int x = event.getClickedBlock().getX();
            int y = event.getClickedBlock().getY();
            int z = event.getClickedBlock().getZ();
            Player e = event.getPlayer();
            String user = e.getUniqueId().toString();
            PreparedStatement ps = con.prepareStatement("SELECT id FROM lock_barrel_position WHERE x = ? AND y = ? AND z = ?");
            ps.setInt(1, x);
            ps.setInt(2, y);
            ps.setInt(3, z);
            ResultSet result = ps.executeQuery();
            int id = -1;
            while(result.next()) {
                id =  result.getInt("id");
            }
            if (id == -1){
                return;
            }
            result.close();
            ps.close();
            //idをもとにuserがあるかどうかを確認
            ps = con.prepareStatement("SELECT id FROM lock_barrel_user WHERE barrel_id=? AND user=?");
            ps.setInt(1, id);
            ps.setString(2, user);
            result = ps.executeQuery();
            boolean ans = false;
            while (result.next()) {
                ans = true;
            }
            result.close();
            ps.close();
            if(!ans){
                event.setCancelled(true);
                event.getPlayer().sendMessage("このバレルはロックされています");
            }
        }
    }

    @EventHandler
    public void setBarrelLock(BlockPlaceEvent event) throws SQLException {
        if (event.getBlock().getType().toString().equals("BARREL")){
            int x = event.getBlock().getX();
            int y = event.getBlock().getY();
            int z = event.getBlock().getZ();
            Player e = event.getPlayer();
            String user = e.getUniqueId().toString();
            PreparedStatement ps = con.prepareStatement("INSERT INTO lock_barrel_position (x, y, z) VALUES (?, ?, ?)");
            ps.setInt(1, x);
            ps.setInt(2, y);
            ps.setInt(3, z);
            ps.executeUpdate();
            ps = con.prepareStatement("SELECT id FROM lock_barrel_position WHERE x = ? AND y = ? AND z = ?");
            ps.setInt(1, x);
            ps.setInt(2, y);
            ps.setInt(3, z);
            ResultSet result = ps.executeQuery();
            int id = -1;
            while(result.next()) {
                id =  result.getInt("id");
            }
            if (id == -1){
                return;
            }
            result.close();
            ps.close();
            ps = con.prepareStatement("INSERT INTO lock_barrel_user (barrel_id, user) VALUES (?, ?)");
            ps.setInt(1, id);
            ps.setString(2, user);
            ps.executeUpdate();
            event.getPlayer().sendMessage("この樽をロックしました");
            ps.close();
        }
    }
    @EventHandler
    //樽を破壊する
    public void breakBarrelLock(BlockBreakEvent event) throws SQLException{
        if (event.getBlock().getType().toString().equals("BARREL")){
            int x = event.getBlock().getX();
            int y = event.getBlock().getY();
            int z = event.getBlock().getZ();
            Player e = event.getPlayer();
            String user = e.getUniqueId().toString();
            //壊した樽のidを取得
            PreparedStatement ps = con.prepareStatement("SELECT id FROM lock_barrel_position WHERE x = ? AND y = ? AND z = ?");
            ps.setInt(1, x);
            ps.setInt(2, y);
            ps.setInt(3, z);
            ResultSet result = ps.executeQuery();
            int id = -1;
            while(result.next()) {
                id =  result.getInt("id");
            }
            result.close();
            ps.close();
            ps = con.prepareStatement("select user from lock_barrel_user where barrel_id = ?");
            ps.setInt(1, id);
            result = ps.executeQuery();
            boolean ans = false;
            while(result.next()) {
                if (user.equals(result.getString("user"))){
                    ans = true;
                    break;
                }
            }
            result.close();
            ps.close();
            if (ans || event.getPlayer().isOp()) {
                ps = con.prepareStatement("DELETE FROM lock_barrel_position WHERE id = ?");
                ps.setInt(1, id);
                ps.executeUpdate();
                ps = con.prepareStatement("DELETE FROM lock_barrel_user WHERE barrel_id = ?");
                ps.setInt(1, id);
                ps.executeUpdate();
                e.sendMessage("樽を破壊しました");
            }else{
                event.setCancelled(true);
                e.sendMessage("この樽はロックされています");
            }
        }
    }

    //データベースに接続
    Connection con = DriverManager.getConnection(
            "jdbc:mysql://localhost/plugin_db?useSSL=false",
            "root",
            "ヒミツ"
    );

}
