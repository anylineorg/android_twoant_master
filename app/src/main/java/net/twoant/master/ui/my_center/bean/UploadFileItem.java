package net.twoant.master.ui.my_center.bean;

import java.io.Serializable;

/**
 * Created by kaiguokai on 2016/10/26.
 */

public class UploadFileItem implements Serializable {
    private static final long serialVersionUID = 1L;

    // The form field name in a form used foruploading a file,

    // such as "upload1" in "<inputtype="file" name="upload1"/>"

    private String formFieldName;

    // File name to be uploaded, thefileName contains path,

    // such as "E:\\some_file.jpg"

    private String fileName;

    public UploadFileItem(String formFieldName, String fileName)

    {

        this.formFieldName = formFieldName;

        this.fileName = fileName;

    }

    public String getFormFieldName()

    {

        return formFieldName;

    }

    public void setFormFieldName(String formFieldName)

    {

        this.formFieldName = formFieldName;

    }

    public String getFileName()

    {

        return fileName;

    }

    public void setFileName(String fileName)

    {

        this.fileName = fileName;

    }
}