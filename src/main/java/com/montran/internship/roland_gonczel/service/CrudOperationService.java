package com.montran.internship.roland_gonczel.service;


import java.util.List;

/**
 *
 * @param <T> is the Dto
 * @param <H> is the history object
 */
public interface CrudOperationService<T,H> {

     List<H> findAllApproveRequests();

     List<T> viewAllActive();

     void addNewEntry(T t);

     void modifyEntry(T t, String id);

     void deleteEntry(String id);

     void approveEntry(String id);

     void rejectEntry(String id);

     void addFromHistory(H h);

     void modifyFromHistory(H h);

     void deleteFromHistory(H h);
}
