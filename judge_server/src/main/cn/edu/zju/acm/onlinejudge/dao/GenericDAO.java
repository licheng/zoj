package cn.edu.zju.acm.onlinejudge.dao;

public interface GenericDAO {
    void beginTransaction();

    void commitTransaction();

    void rollbackTransaction();

    void closeSession();
}
