package com.silita.biaodaa.dao_temp;

import com.silita.biaodaa.common.jdbc.JdbcBase;
import com.silita.biaodaa.common.jdbc.Page;
import com.silita.biaodaa.model.MessagePush;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gmy on 2017/8/29.
 */
@Deprecated
@Repository
public class MessagePushDAOImpl extends JdbcBase implements MessagePushDAO {

    @Override
    public List<Map<String, Object>> queryCollecNoticeForList(List<String> idList) {
        StringBuffer sql = new StringBuffer(" SELECT `userId`, `noticeId`, `title` " +
                " FROM mishu_write.collec_notice  " +
                " WHERE date_format(collecTime, '%y-%m-%d') > DATE_SUB(CURDATE(), INTERVAL 3 MONTH) " +
                "  AND userid !='' AND userid is not null ");
        if(idList !=null && idList.size()>0) {
            sql.append(" AND (");
            StringBuffer filterSql = new StringBuffer();
            for (String id : idList) {
                filterSql.append(" or noticeId='"+ id +"'");
            }
            sql.append(filterSql.delete(0,3)).append(")");
        }
        sql.append("ORDER BY ID DESC ");
        return this.getJdbcTemplate().queryForList(sql.toString());
    }

    @Override
    public void insertSendMessage(String userId, String mainId, String relationId, String snatchUrl, String title) {
        String sql = "INSERT INTO mishu.message_push(`userId`, `mainId`, `relationId`, `snatchUrl`, `title`, `createDate`, `is_send`, `is_system`) VALUES(?, ?, ?, ?, ?, NOW(), 0, 1)";
        this.getJdbcTemplate().update(sql, userId, mainId, relationId, snatchUrl, title);
//        return result > 0;
    }

    @Override
    public List<Map<String, Object>> queryMessagePushForList() {
//        "WHERE mainId <> '' AND relationId <> '' " +
        String sql = "SELECT `userId`, `mainId`, `relationId`, `title`, `createDate`, `is_send` " +
                "FROM mishu.message_push " +
                "WHERE is_system = 1 AND is_send = 0 " +
                "ORDER BY id DESC ";
        return this.getJdbcTemplate().queryForList(sql);
    }

    @Override
    public String getUserPhoneByUserId(String userId) {
        String sql = "SELECT `userPhone` FROM mishu_write.user_temp WHERE userId = ?";
        return this.getJdbcTemplate().queryForObject(sql, new Object[]{userId}, String.class);
    }

    @Override
    public List<Map<String, Object>> getNoticeTitleById(String noticeid) {
        String sql = "SELECT `url`, `title`, `type` FROM mishu.snatchurl WHERE id = ?";
        return this.getJdbcTemplate().queryForList(sql, noticeid);
    }

    @Override
    public void UpdateIsSendByUserId(String userId) {
        String sql = "UPDATE mishu.message_push SET is_send = 1 WHERE userId = ?";
        this.getJdbcTemplate().update(sql, userId);
//        return result > 0;
    }

    @Override
    public void UpdateMessageByUserId(String message, String relationId, int type) {
        String sql = "UPDATE mishu.message_push SET message = ?, is_send = 1, type = ? WHERE relationId = ?";
        this.getJdbcTemplate().update(sql, message, type, relationId);
    }

    @Override
    public Page getSystempMessage(String userId) {
        String sql = "SELECT `title`, `message`, `createDate` FROM mishu.message_push WHERE is_system = 0 ";
        List<String> params=new ArrayList<String>();
        if( this.vertify(userId)){
            sql += " AND userId=?";
            params.add(userId);
        }
        return this.queryForPage(sql, params, MessagePush.class);
    }

    @Override
    public Page getChangeMessage(String userId) {
        String sql = "SELECT `mainId`, `relationId`, `snatchUrl`, `message`, `createDate`, `type` FROM mishu.message_push WHERE is_send = 1 AND is_system = 1 ";
        List<String> params=new ArrayList<String>();
        if( this.vertify(userId)){
            sql += " AND userId=?";
            params.add(userId);
        }
        return this.queryForPage(sql, params, MessagePush.class);
    }

    @Override
    public int getMessageCountByMainIdAndRelationId(String mainId, String relationId) {
        String sql = "SELECT COUNT(1) FROM mishu.message_push WHERE mainId = ? AND relationId = ?";
        return this.getJdbcTemplate().queryForObject(sql, new Object[]{mainId, relationId},Integer.class);
    }

}
