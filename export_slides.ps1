$ppt = New-Object -ComObject PowerPoint.Application
$pres = $ppt.Presentations.Open('D:\AOPD Project\FleetWise_Hub_Presentation.pptx', [Microsoft.Office.Core.MsoTriState]::msoFalse, [Microsoft.Office.Core.MsoTriState]::msoFalse, [Microsoft.Office.Core.MsoTriState]::msoFalse)
$outDir = 'D:\AOPD Project\slides_exported'
if (!(Test-Path $outDir)) {
    New-Item -ItemType Directory -Path $outDir
}
$i = 1
foreach ($slide in $pres.Slides) {
    $outPath = Join-Path $outDir "slide_$i.png"
    $slide.Export($outPath, "PNG", 1920, 1080)
    $i++
}
$pres.Close()
$ppt.Quit()
Write-Output "Exported all slides successfully"
