package io.novelis.filtragecv;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.models.auth.In;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Keyword {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    private String name;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private String category;
    private Integer count;

    public Integer getSimilarSkillCount() {
        return similarSkillCount;
    }

    public void setSimilarSkillCount(Integer similarSkillCount) {
        this.similarSkillCount = similarSkillCount;
    }

    private Integer similarSkillCount;

    public Keyword(String name, String category, Integer count, Integer similarSkillCount) {
        this.name = name;
        this.count = count;
        this.similarSkillCount = similarSkillCount;
        this.category = category;
    }
    public Keyword(String name, String category) {
        this.name = name;
        this.category = category;
    }
}
