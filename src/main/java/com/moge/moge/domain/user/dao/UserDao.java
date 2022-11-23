package com.moge.moge.domain.user.dao;

import com.moge.moge.domain.user.model.User;
import com.moge.moge.domain.user.model.req.*;
import com.moge.moge.domain.user.model.res.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int createUser(PostUserReq postUserReq) {
        String createUserQuery = "insert into User(email, nickname, password) values(?,?,?)";
        Object[] createUserParams = new Object[] {
                postUserReq.getEmail(),
                postUserReq.getNickname(),
                postUserReq.getPassword()
        };
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInsertIdQuery = "select last_insert_id()";
        int userIdx = this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);

        String createUserTermsQuery = "insert into Terms(contract1, contract2, contract3, contract4, userIdx) values(?,?,?,?,?)";
        Object[] createUserTermsParams = new Object[] {
                postUserReq.getContract1(),
                postUserReq.getContract2(),
                postUserReq.getContract3(),
                postUserReq.getContract4(),
                userIdx
        };
        this.jdbcTemplate.update(createUserTermsQuery, createUserTermsParams);
        return userIdx;
    }

    public PostUserKeywordRes createUserKeyword(int userIdx, PostUserKeywordReq postUserKeywordReq) {
        int[] ints = postUserKeywordReq.getCategoryIdx().stream().mapToInt(Integer::intValue).toArray(); // Integer -> int[]
        for (int category : ints) {
            String createUserKeywordQuery = "insert into UserCategory(userIdx, categoryIdx) values(?,?)";
            Object[] createUserKeywordParams = new Object[] {
                    userIdx,
                    category
            };
            this.jdbcTemplate.update(createUserKeywordQuery, createUserKeywordParams);
        }
        return new PostUserKeywordRes("");
    }

    public int updateUserKeyword(int userCategoryIdx, int index) {
        String updateQuery = "update UserCategory set categoryIdx =? where userCategoryIdx =?";
        Object[] params = new Object[]{index, userCategoryIdx};
        return this.jdbcTemplate.update(updateQuery, params);
    }

    public List<Integer> getUserCategoryIdx(int userIdx) {
        String query = "select userCategoryIdx from UserCategory where userIdx = ?";
        return this.jdbcTemplate.query(query,
                (rs, rowNum) -> new Integer(rs.getInt("userCategoryIdx")), userIdx);
    }

    public int checkEmail(String email) {
        String checkEmailQuery = "select exists(select email from User where email = ? and status != 'DELETE')";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery, int.class, checkEmailParams);
    }

    public int checkNickname(String nickname) {
        String checkNicknameQuery = "select exists(select nickname from User where nickname = ? and status != 'DELETE')";
        String checkNicknameParams = nickname;
        return this.jdbcTemplate.queryForObject(checkNicknameQuery, int.class, checkNicknameParams);
    }

    public User getPwd(PostLoginReq postLoginReq){
        String getPwdQuery = "select userIdx, email, nickname, password, profileImage from User where email = ? and status = 'ACTIVE'";
        String getPwdParams = postLoginReq.getEmail();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs,rowNum)-> new User(
                        rs.getInt("userIdx"),
                        rs.getString("email"),
                        rs.getString("nickname"),
                        rs.getString("password"),
                        rs.getString("profileImage")
                ),
                getPwdParams
        );
    }

    public GetUserRes getUser(int userIdx) {
        String getUserQuery = "select userIdx, email, password, nickname, profileImage, status, createdAt from User where userIdx =?";
        int getUserParam = userIdx;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("nickname"),
                        rs.getString("profileImage"),
                        rs.getString("status"),
                        rs.getString("createdAt")
                ),
                getUserParam);
    }


    public int insertCertifiedCode(String email, String code) {
        String checkQuery = "select exists(select * from Certification where email =?)";
        String insertQuery = "insert into Certification(email, code) values (?,?)";
        String updateQuery = "update Certification set code =? where email =?";

        if (this.jdbcTemplate.queryForObject(checkQuery, int.class, email) == 0) {
            return this.jdbcTemplate.update(insertQuery, email, code);
        }
        return this.jdbcTemplate.update(updateQuery, code, email);
    }

    public int checkCertifiedEmail(String email) {
        String checkQuery = "select exists(select * from Certification where email = ?)";
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, email);
    }

    public int checkCertifiedTime(String email) {
        String checkQuery = "select TIMESTAMPDIFF(second, updatedAt, CURRENT_TIMESTAMP()) from Certification where email =?";
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, email);
    }

    public boolean checkCertifiedCode(String email, String code) {
        String checkQuery = "select exists(select * from Certification where email =? and code =?)";
        if (this.jdbcTemplate.queryForObject(checkQuery, int.class, email, code) == 0) {
            return false;
        } else {
            return true;
        }
    }

    public int checkCategoryExists(int categoryIdx) {
        String checkQuery = "select exists(select * from Category where status = 'ACTIVE' and categoryIdx =?)";
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, categoryIdx);
    }

    public int checkUserCategoryExists(int userIdx) {
        String checkQuery = "select exists(select * from UserCategory where status = 'ACTIVE' and userIdx =?)";
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, userIdx);
    }

    public int updatePassword(int userIdx, PatchUserPasswordReq patchUserPasswordReq) {
        String updatePasswordQuery = "update User set password = ? where userIdx =? and status = 'ACTIVE'";
        Object[] updatePasswordParams = new Object[]{
                patchUserPasswordReq.getModPassword(),
                userIdx
        };
        return this.jdbcTemplate.update(updatePasswordQuery, updatePasswordParams);
    }

    public int deleteUser(int userIdx) {
        String deleteUserQuery = "update User set status = 'DELETE' where userIdx=?";
        return this.jdbcTemplate.update(deleteUserQuery, userIdx);
    }

    public int updateUserProfile(int userIdx, String url, String nickname) {
        String updateUserProfileQuery = "update User set profileImage =?, nickname =? where userIdx =? and status = 'ACTIVE'";
        Object[] params = new Object[]{url, nickname, userIdx};
        return this.jdbcTemplate.update(updateUserProfileQuery, params);
    }

    public String getUserProfileImage(int userIdx) {
        String getUserProfileImageQuery = "select profileImage from User where userIdx =? and status = 'ACTIVE'";
        return this.jdbcTemplate.queryForObject(getUserProfileImageQuery, (rs, rowNum) -> new String("profileImage"), userIdx);
    }

    public void deleteUserProfileImage(int userIdx) {
        String deleteUserProfileImageQuery = "update User set profileImage = null where userIdx =? and status = 'ACTIVE'";
        this.jdbcTemplate.update(deleteUserProfileImageQuery, userIdx);
    }

    public int createUserFollow(int userIdx, int followingIdx) {
        String createUserFollowQuery = "insert into Follow(followerIdx, followingIdx) values(?,?)";
        Object[] params = new Object[]{userIdx, followingIdx };
        return this.jdbcTemplate.update(createUserFollowQuery, params);
    }

    public int checkUserFollowExists(int userIdx, int followingIdx) {
        String checkUserFollowExistsQuery = "select exists(select * from Follow where followerIdx =? and followingIdx =? and status ='ACTIVE')";
        Object[] params = new Object[] {userIdx, followingIdx};
        return this.jdbcTemplate.queryForObject(checkUserFollowExistsQuery, int.class, params);
    }

    public int checkUserExists(int followingIdx) {
        String checkUserExistsQuery = "select exists(select * from User where userIdx =? and (status ='ACTIVE' or status = 'NAVER' or status = 'KAKAO'))";
        int param = followingIdx;
        return this.jdbcTemplate.queryForObject(checkUserExistsQuery, int.class, param);
    }

    public int checkUserFollowStatus(int userIdx, int followingIdx) {
        String checkUserFollowStatusQuery = "select exists(select * from Follow where followerIdx =? and followingIdx =? and status = 'DELETE')";
        Object[] params = new Object[] {userIdx, followingIdx};
        return this.jdbcTemplate.queryForObject(checkUserFollowStatusQuery, int.class, params);
    }

    public int updateUserFollowStatus(int userIdx, int followingIdx) {
        String deleteUserFollowQuery = "update Follow set status = 'ACTIVE' where followerIdx =? and followingIdx =?";
        Object[] params = new Object[]{userIdx, followingIdx};
        return this.jdbcTemplate.update(deleteUserFollowQuery, params);
    }

    public int deleteUserFollow(int userIdx, int followingIdx) {
        String deleteUserFollowQuery = "update Follow set status = 'DELETE' where followerIdx =? and followingIdx =?";
        Object[] params = new Object[]{userIdx, followingIdx};
        return this.jdbcTemplate.update(deleteUserFollowQuery, params);
    }

    public List<GetUserFollowRes> getUserFollowings(int userIdx, int page) {
        String getUserFollowingsQuery =
                "select U.userIdx, U.nickname, U.profileImage\n" +
                "from User U\n" +
                "    left join Follow F on U.userIdx = F.followingIdx\n" +
                "where (U.status = 'ACTIVE' or U.status = 'NAVER' or U.status = 'KAKAO')" +
                "and userIdx in (select followingIdx from Follow where followerIdx =?)\n" +
                "order by F.createdAt desc limit 5 offset ?;\n";

        Object[] params = new Object[]{userIdx, (page-1)*5};
        return this.jdbcTemplate.query(getUserFollowingsQuery,
                (rs, rowNum) -> new GetUserFollowRes(
                        rs.getInt("userIdx"),
                        rs.getString("nickname"),
                        rs.getString("profileImage")
                ),
                params);

    }

    public List<GetUserFollowRes> getUserFollowers(int userIdx, int page) {
        String getUserFollowersQuery =
                "select userIdx, nickname, profileImage \n" +
                "    from User U\n" +
                "    left join Follow F on U.userIdx = F.followingIdx \n" +
                "where (U.status = 'ACTIVE' or U.status = 'NAVER' or U.status = 'KAKAO') " +
                "and userIdx in (select followerIdx from Follow where followingIdx = ?) \n" +
                "order by F.createdAt asc limit 5 offset ?";

        Object[] params = new Object[]{userIdx, (page-1)*5};
        return this.jdbcTemplate.query(getUserFollowersQuery,
                (rs, rowNum) -> new GetUserFollowRes(
                        rs.getInt("userIdx"),
                        rs.getString("nickname"),
                        rs.getString("profileImage")
                ), params);
    }

    public int checkUser(int userIdx) {
        String Query = "select exists(select * from User where (status = 'ACTIVE' or status = 'NAVER' or status = 'KAKAO') and userIdx =? )";
        return this.jdbcTemplate.queryForObject(Query, int.class, userIdx);
    }

    public GetUserProfileRes getUserProfile(int userIdx) {
        String getUserProfileQuery =
                "select userIdx, nickname, profileImage, userPoint,\n" +
                "    (select count(*) from Follow F where F.followingIdx = U.userIdx) as followerCount,\n" +
                "    (select count(*) from Follow F where F.followerIdx = U.userIdx) as followingCount\n" +
                "from User U\n" +
                "where U.userIdx = ? and U.status != 'DELETE';";
        return this.jdbcTemplate.queryForObject(getUserProfileQuery,
                (rs, rowNum) -> new GetUserProfileRes(
                        rs.getInt("userIdx"),
                        rs.getString("nickname"),
                        rs.getString("profileImage"),
                        rs.getInt("userPoint"),
                        rs.getInt("followerCount"),
                        rs.getInt("followingCount")
                ), userIdx);
    }

    public List<GetUserBoardLikeRes> getUserBoardLike(int userIdx) {
        String getUserBoardLikeQuery =
                "select \n" +
                "    concat('#',categoryName) as categoryName, \n" +
                "    title, \n" +
                "    (select count(*) from Quiz Q where Q.boardIdx = B.boardIdx) as quizCount,\n" +
                "    B.viewCount as viewCount,\n" +
                "    (select count(*) from BoardLike BL where BL.boardIdx = B.boardIdx) as likeCount\n" +
                "from Board B\n" +
                "    left join Category C   on C.categoryIdx = B.categoryIdx\n" +
                "    left join BoardLike BL on BL.boardIdx = B.boardIdx\n" +
                "    left join User U on U.userIdx = BL.userIdx\n" +
                "where BL.userIdx = ? and BL.status = 'ACTIVE' order by BL.createdAt desc";

        return this.jdbcTemplate.query(getUserBoardLikeQuery,
                (rs,rowNum) -> new GetUserBoardLikeRes(
                        rs.getString("categoryName"),
                        rs.getString("title"),
                        rs.getInt("quizCount"),
                        rs.getInt("viewCount"),
                        rs.getInt("likeCount")
                ), userIdx);
    }
    public int checkUserComment(int userIdx, int commentIdx, int boardIdx) {
        String checkUserCommentQuery = "select exists(select * from Comment where userIdx =? and commentIdx =? and boardIdx = ?)";
        Object[] params = new Object[]{userIdx, commentIdx, boardIdx};
        return this.jdbcTemplate.queryForObject(checkUserCommentQuery, int.class, params);
    }

    public List<GetUserKeywordRes> getUserKeyword(int userIdx) {
        String getUserKeywordQuery =
                "select C.categoryIdx, C.categoryName, C.categorySubName\n" +
                "from Category C\n" +
                "    left join UserCategory UC on UC.categoryIdx = C.categoryIdx\n" +
                "    left join User U on U.userIdx = UC.userIdx\n" +
                "where U.userIdx = ?";
        return this.jdbcTemplate.query(getUserKeywordQuery,
                (rs, rowNum) -> new GetUserKeywordRes(
                        rs.getInt("categoryIdx"),
                        rs.getString("categoryName"),
                        rs.getString("categorySubName")
                )
                ,userIdx);
    }
}
