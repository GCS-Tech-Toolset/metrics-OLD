/****************************************************************************
 * Author: kgoldstein
 * Date: Feb 6, 2022
 * 
 * NOTICE: All information contained herein is, and remains
 * the property of Kevin A. Goldstein R.
 * The intellectual and technical concepts contained
 * herein are proprietary to the author and may be covered by U.S.
 * and Foreign Patents, patents in process, and are protected by
 * trade secret or copyright law.
 * Dissemination, use or reproduction of this material is strictly
 * forbidden unless prior written permission is
 * obtained from the author.
 ****************************************************************************/





package com.kagr.metrics.cfg;





public class RegistryNotReadyException extends RuntimeException
{



    private static final long serialVersionUID = 1L;





    public RegistryNotReadyException(String message_)
    {
        super(message_);

    }





    public RegistryNotReadyException(Throwable cause_)
    {
        super(cause_);

    }





    public RegistryNotReadyException(String message_, Throwable cause_)
    {
        super(message_, cause_);

    }





    public RegistryNotReadyException(String message_, Throwable cause_, boolean enableSuppression_, boolean writableStackTrace_)
    {
        super(message_, cause_, enableSuppression_, writableStackTrace_);

    }

}

