Dim objProgressMsg
Function ProgressMsg( strMessage, strWindowTitle )
' Written by Denis St-Pierre
    Set wshShell = WScript.CreateObject( "WScript.Shell" )
    strTEMP = wshShell.ExpandEnvironmentStrings( "%TEMP%" )
    If strMessage = "" Then
        On Error Resume Next
        objProgressMsg.Terminate( )
        On Error Goto 0
        Exit Function
    End If
    Set objFSO = CreateObject("Scripting.FileSystemObject")
    strTempVBS = strTEMP + "\" & "Message.vbs"

    Set objTempMessage = objFSO.CreateTextFile( strTempVBS, True )
    objTempMessage.WriteLine( "MsgBox""" & strMessage & """, 4096, """ & strWindowTitle & """" )
    objTempMessage.Close

    On Error Resume Next
    objProgressMsg.Terminate( )
    On Error Goto 0

    Set objProgressMsg = WshShell.Exec( "%windir%\system32\wscript.exe " & strTempVBS )

    Set wshShell = Nothing
    Set objFSO   = Nothing
End Function

 ProgressMsg "Please wait...", "Opening url..."

Set objParametros = WScript.Arguments

url = "about:blank"
x = 160
y = 20
w = 1280
h = 1024
 
If objParametros.Count > 0 Then
	url = objParametros.Item(0)
End If
If objParametros.Count > 1 Then
	x = objParametros.Item(1)
End If
If objParametros.Count > 2 Then
	y = objParametros.Item(2)
End If
If objParametros.Count > 3 Then
	w = objParametros.Item(3)
End If
If objParametros.Count > 4 Then
	h = objParametros.Item(4)
End If

 url = Replace(url,"#","&")
 url = Replace(url,"$","^")
 'x=MsgBox(url)

 Set ie = CreateObject("InternetExplorer.Application")
 'Header = "User-Agent:NavEmb"
 'ie.FullScreen = False
 ie.MenuBar = False
 ie.Toolbar = False
 ie.StatusBar = False
 ie.AddressBar = False
 ie.Height = h
 ie.Width = w
 ie.Left = x
 ie.Top = y
 ie.Visible = True
 ProgressMsg "Please wait...", "Opening url..."
 
 'WScript.Sleep 100
 CreateObject("WScript.Shell").AppActivate "Internet Explorer" 'Para traer el navegador al frente (hay que hacerlo antes del navigate)
 
 ie.Navigate url

 'Mantenemos el bucle porque sino no coge bien el foco el navegador para traerlo al frente
 Do While ie.Busy : WScript.Sleep 100 : Loop
 ProgressMsg "Please wait...", "Opening url..."
 'WScript.Sleep 100
 
 on error resume next
 CreateObject("WScript.Shell").AppActivate ie.document.title

 
