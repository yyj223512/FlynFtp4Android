package com.flyn.ftp;

public class CustomFtpExcetion extends Exception
{


    /**
	 * 
	 */
	private static final long serialVersionUID = -6758805973960966790L;

	public CustomFtpExcetion()
    {
        super();
    }

    public CustomFtpExcetion(Throwable cause)
    {
        super(cause);
    }

    public CustomFtpExcetion(String exceptionMessage)
    {
        super(exceptionMessage);
    }

    public CustomFtpExcetion(String exceptionMessage, Throwable reason)
    {
        super(exceptionMessage, reason);
    }
}
