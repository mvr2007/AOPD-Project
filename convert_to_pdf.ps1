$ppt = New-Object -ComObject PowerPoint.Application
$pres = $ppt.Presentations.Open('D:\AOPD Project\FleetWise_Hub_Presentation.pptx', [Microsoft.Office.Core.MsoTriState]::msoFalse, [Microsoft.Office.Core.MsoTriState]::msoFalse, [Microsoft.Office.Core.MsoTriState]::msoFalse)
$pdfPath = 'D:\AOPD Project\FleetWise_Hub_Presentation.pdf'
# 32 corresponds to ppSaveAsPDF
$pres.SaveAs($pdfPath, 32)
$pres.Close()
$ppt.Quit()
Write-Output "PDF saved successfully: $pdfPath"
