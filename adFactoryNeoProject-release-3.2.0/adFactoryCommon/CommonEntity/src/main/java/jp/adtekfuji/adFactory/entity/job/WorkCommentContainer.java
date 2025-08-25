/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package jp.adtekfuji.adFactory.entity.job;

import adtekfuji.utility.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import jp.adtekfuji.adFactory.entity.master.ServiceInfoEntity;
import jp.adtekfuji.adFactory.utility.JsonUtils;

/**
 * 作業コメントコンテナ
 * 
 * @author s-heya
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class WorkCommentContainer {
    @JsonProperty("nextId")
    private Long nextId;

    @JsonProperty("comments")
    private List<WorkComment> comments;

    /**
     * コンストラクタ
     */
    public WorkCommentContainer() {
        this.nextId = 1L;
        this.comments = new ArrayList<>();
    }

    /**
     * 次のコメントIDを取得する。
     * 
     * @return 次のコメントID
     */
    public Long getNextId() {
        return nextId;
    }

    /**
     * 次のコメントIDを設定する。
     * 
     * @param nextId 次のコメントID
     */
    private void setNextId(Long nextId) {
        this.nextId = nextId;
    }

    /**
     * 
     * 
     * @return 
     */
    public List<WorkComment> getComments() {
        return comments;
    }

    /**
     * 
     * @param comments 
     */
    private void setComments(List<WorkComment> comments) {
        this.comments = comments;
    }
    
    /**
     * 作業コメントを追加する。
     * 
     * @param comment 
     */
    public void add(WorkComment comment) {
        comment.setId(this.nextId);
        comment.setDel(false);
        this.comments.add(comment);
        this.nextId++;
    }
    
    /**
     * 作業コメントを取得する。
     * 
     * @param commentId
     * @return 
     */
    public WorkComment get(Long commentId) {
        Optional<WorkComment> opt = this.comments.stream()
                .filter(o -> Objects.equals(o.getId(), commentId))
                .findFirst();
        return opt.isPresent() ? opt.get() : null;
    }
    
    /**
     * 作業コメントを削除する。
     * 
     * @param commentId コメントID
     */
    public boolean remove(Long commentId) {
        Optional<WorkComment> opt = this.comments.stream()
                .filter(o -> Objects.equals(o.getId(), commentId))
                .findFirst();
        if (opt.isPresent()) {
            opt.get().setDel(true);
            return true;
        }
        return false;
    }

    /**
     * コンストラクタ
     * 
     * @param map 
     */
    public WorkCommentContainer(Map<String, Object> map) {
        this.nextId = (Long) ((Integer) map.get("nextId")).longValue();

        ArrayList<Map<String, Object>> _list = (ArrayList) map.get("comments");
        this.comments = _list.stream()
                .map(o -> new WorkComment(o))
                .collect(Collectors.toList());
    }

    /**
     * JSON文字列から集荷実績情報を取得する。
     * 
     * @param serviceInfosStr
     * @return WorkCommentContainer
     */
    public static WorkCommentContainer lookup(String serviceInfosStr) {
        if (StringUtils.isEmpty(serviceInfosStr)) {
            return null;
        }
        
        try {
            Optional<ServiceInfoEntity> opt  = JsonUtils.jsonToObjects(serviceInfosStr, ServiceInfoEntity[].class).stream()
                    .filter(o -> ServiceInfoEntity.SERVICE_INFO_COMMENTS.equals(o.getService()) && Objects.nonNull(o.getJob()))
                    .findFirst();
            if (opt.isPresent()) {
                LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) opt.get().getJob();
                return new WorkCommentContainer(map);
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }
}
