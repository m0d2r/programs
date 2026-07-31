Add-Type -AssemblyName System.Windows.Forms
Add-Type -AssemblyName System.Drawing

# Language form
$lf = New-Object System.Windows.Forms.Form
$lf.Text = "VM Launcher"
$lf.Size = New-Object System.Drawing.Size(320, 260)
$lf.StartPosition = "CenterScreen"
$lf.BackColor = [System.Drawing.Color]::FromArgb(30, 30, 30)
$lf.FormBorderStyle = "FixedDialog"

$l = New-Object System.Windows.Forms.Label
$l.Text = "VM LAUNCHER"
$l.Font = New-Object System.Drawing.Font("Arial", 14, [System.Drawing.FontStyle]::Bold)
$l.ForeColor = [System.Drawing.Color]::White
$l.Location = New-Object System.Drawing.Point(65, 20)
$lf.Controls.Add($l)

$s = New-Object System.Windows.Forms.Label
$s.Text = "Select language"
$s.ForeColor = [System.Drawing.Color]::FromArgb(130, 130, 130)
$s.Location = New-Object System.Drawing.Point(85, 50)
$lf.Controls.Add($s)

$sel = $null

$b1 = New-Object System.Windows.Forms.Button
$b1.Text = "English"
$b1.Size = New-Object System.Drawing.Size(200, 35)
$b1.Location = New-Object System.Drawing.Point(50, 85)
$b1.BackColor = [System.Drawing.Color]::FromArgb(0, 100, 200)
$b1.ForeColor = [System.Drawing.Color]::White
$b1.Add_Click({ $script:sel = "en"; $lf.Close() })
$lf.Controls.Add($b1)

$b2 = New-Object System.Windows.Forms.Button
$b2.Text = "Cestina"
$b2.Size = New-Object System.Drawing.Size(200, 35)
$b2.Location = New-Object System.Drawing.Point(50, 125)
$b2.BackColor = [System.Drawing.Color]::FromArgb(0, 140, 60)
$b2.ForeColor = [System.Drawing.Color]::White
$b2.Add_Click({ $script:sel = "cs"; $lf.Close() })
$lf.Controls.Add($b2)

$b3 = New-Object System.Windows.Forms.Button
$b3.Text = "Slovencina"
$b3.Size = New-Object System.Drawing.Size(200, 35)
$b3.Location = New-Object System.Drawing.Point(50, 165)
$b3.BackColor = [System.Drawing.Color]::FromArgr(180, 110, 0)
$b3.ForeColor = [System.Drawing.Color]::White
$b3.Add_Click({ $script:sel = "sk"; $lf.Close() })
$lf.Controls.Add($b3)

$lf.ShowDialog()
if (-not $script:sel) { exit }

# Translations
if ($script:sel -eq "en") {
    $t = @{ isoL="ISO FILE"; browse="Browse"; hw="HARDWARE"; arch="Architecture"; ram="RAM"; cpu="CPU cores"; mach="Machine"; cpuT="CPU"; mouse="Mouse"; kb="Keyboard"; boot="BOOT"; bootIso="Boot from ISO"; bootDisk="Boot from Disk"; diskL="Disk Image"; newDisk="Create Disk"; start="START"; ready="Ready"; isoErr="Select ISO file!"; diskErr="Select disk!"; created="Disk created"; done="Done"; err="Error" }
} elseif ($script:sel -eq "cs") {
    $t = @{ isoL="ISO SOUBOR"; browse="Prochazet"; hw="HARDWARE"; arch="Architektura"; ram="RAM"; cpu="CPU jadro"; mach="Masina"; cpuT="CPU"; mouse="Mys"; kb="Klavesnice"; boot="BOOT"; bootIso="Boot z ISO"; bootDisk="Boot z disku"; diskL="Disk image"; newDisk="Vytvorit disk"; start="SPUSTIT"; ready="Ready"; isoErr="Vyberte ISO!"; diskErr="Vyberte disk!"; created="Disk vytvoren"; done="Hotovo"; err="Chyba" }
} else {
    $t = @{ isoL="ISO SUBOR"; browse="Prehladat"; hw="HARDWARE"; arch="Architektura"; ram="RAM"; cpu="CPU jadro"; mach="Masina"; cpuT="CPU"; mouse="Mys"; kb="Klavesnica"; boot="BOOT"; bootIso="Boot z ISO"; bootDisk="Boot z disku"; diskL="Disk image"; newDisk="Vytvorit disk"; start="SPUSTIT"; ready="Ready"; isoErr="Vyberte ISO!"; diskErr="Vyberte disk!"; created="Disk vytvoreny"; done="Hotovo"; err="Chyba" }
}

# Main form
$f = New-Object System.Windows.Forms.Form
$f.Text = "VM Launcher"
$f.Size = New-Object System.Drawing.Size(440, 600)
$f.StartPosition = "CenterScreen"
$f.BackColor = [System.Drawing.Color]::FromArgb(30, 30, 30)

# Title
$tl = New-Object System.Windows.Forms.Label
$tl.Text = "VM LAUNCHER"
$tl.Font = New-Object System.Drawing.Font("Arial", 14, [System.Drawing.FontStyle]::Bold)
$tl.ForeColor = [System.Drawing.Color]::White
$tl.Location = New-Object System.Drawing.Point(15, 15)
$f.Controls.Add($tl)

$y = 50

# ISO label
$il = New-Object System.Windows.Forms.Label
$il.Text = $t["isoL"]
$il.Font = New-Object System.Drawing.Font("Arial", 8, [System.Drawing.FontStyle]::Bold)
$il.ForeColor = [System.Drawing.Color]::FromArgb(80, 160, 255)
$il.Location = New-Object System.Drawing.Point(15, $y)
$f.Controls.Add($il)
$y += 18

$it = New-Object System.Windows.Forms.TextBox
$it.Location = New-Object System.Drawing.Point(15, $y)
$it.Size = New-Object System.Drawing.Size(290, 25)
$it.BackColor = [System.Drawing.Color]::FromArgb(45, 45, 55)
$it.ForeColor = [System.Drawing.Color]::White
$f.Controls.Add($it)

$ib = New-Object System.Windows.Forms.Button
$ib.Text = $t["browse"]
$ib.Location = New-Object System.Drawing.Point(310, $y)
$ib.Size = New-Object System.Drawing.Size(90, 25)
$ib.BackColor = [System.Drawing.Color]::FromArgb(60, 60, 75)
$ib.ForeColor = [System.Drawing.Color]::White
$ib.FlatStyle = "Flat"
$ib.Add_Click({ $d = New-Object System.Windows.Forms.OpenFileDialog; $d.Filter = "ISO (*.iso)|*.iso"; if ($d.ShowDialog() -eq "OK") { $it.Text = $d.FileName } })
$f.Controls.Add($ib)
$y += 32

# Hardware label
$hl = New-Object System.Windows.Forms.Label
$hl.Text = $t["hw"]
$hl.Font = New-Object System.Drawing.Font("Arial", 8, [System.Drawing.FontStyle]::Bold)
$hl.ForeColor = [System.Drawing.Color]::FromArgb(80, 160, 255)
$hl.Location = New-Object System.Drawing.Point(15, $y)
$f.Controls.Add($hl)
$y += 18

$hw = New-Object System.Windows.Forms.Panel
$hw.Location = New-Object System.Drawing.Point(15, $y)
$hw.Size = New-Object System.Drawing.Size(385, 100)
$hw.BackColor = [System.Drawing.Color]::FromArgb(38, 38, 48)
$f.Controls.Add($hw)

# Row 1 in hardware panel
$hy = 8

$al = New-Object System.Windows.Forms.Label; $al.Text = $t["arch"]; $al.Font = New-Object System.Drawing.Font("Arial", 7); $al.ForeColor = [System.Drawing.Color]::FromArgb(130, 130, 140); $al.Location = New-Object System.Drawing.Point(8, $hy); $hw.Controls.Add($al)
$ac = New-Object System.Windows.Forms.ComboBox; $ac.Location = New-Object System.Drawing.Point(8, $hy+12); $ac.Size = New-Object System.Drawing.Size(100, 24); $ac.Items.Add("x86_64"); $ac.Items.Add("PowerPC"); $ac.SelectedItem = "x86_64"; $ac.BackColor = [System.Drawing.Color]::FromArgb(55, 55, 65); $ac.ForeColor = [System.Drawing.Color]::White; $hw.Controls.Add($ac)

$rl = New-Object System.Windows.Forms.Label; $rl.Text = $t["ram"]; $rl.Font = New-Object System.Drawing.Font("Arial", 7); $rl.ForeColor = [System.Drawing.Color]::FromArgb(130, 130, 140); $rl.Location = New-Object System.Drawing.Point(120, $hy); $hw.Controls.Add($rl)
$rn = New-Object System.Windows.Forms.NumericUpDown; $rn.Location = New-Object System.Drawing.Point(120, $hy+12); $rn.Size = New-Object System.Drawing.Size(70, 24); $rn.Minimum = 256; $rn.Maximum = 65536; $rn.Value = 2048; $rn.BackColor = [System.Drawing.Color]::FromArgb(55, 55, 65); $rn.ForeColor = [System.Drawing.Color]::White; $hw.Controls.Add($rn)

$cl = New-Object System.Windows.Forms.Label; $cl.Text = $t["cpu"]; $cl.Font = New-Object System.Drawing.Font("Arial", 7); $cl.ForeColor = [System.Drawing.Color]::FromArgb(130, 130, 140); $cl.Location = New-Object System.Drawing.Point(205, $hy); $hw.Controls.Add($cl)
$cn = New-Object System.Windows.Forms.NumericUpDown; $cn.Location = New-Object System.Drawing.Point(205, $hy+12); $cn.Size = New-Object System.Drawing.Size(50, 24); $cn.Minimum = 1; $cn.Maximum = 32; $cn.Value = 4; $cn.BackColor = [System.Drawing.Color]::FromArgb(55, 55, 65); $cn.ForeColor = [System.Drawing.Color]::White; $hw.Controls.Add($cn)

$ml = New-Object System.Windows.Forms.Label; $ml.Text = $t["mach"]; $ml.Font = New-Object System.Drawing.Font("Arial", 7); $ml.ForeColor = [System.Drawing.Color]::FromArgb(130, 130, 140); $ml.Location = New-Object System.Drawing.Point(270, $hy); $hw.Controls.Add($ml)
$mt = New-Object System.Windows.Forms.TextBox; $mt.Location = New-Object System.Drawing.Point(270, $hy+12); $mt.Size = New-Object System.Drawing.Size(100, 24); $mt.Text = "q35"; $mt.BackColor = [System.Drawing.Color]::FromArgb(55, 55, 65); $mt.ForeColor = [System.Drawing.Color]::White; $hw.Controls.Add($mt)

# Row 2 in hardware panel
$hy2 = 55

$ctl = New-Object System.Windows.Forms.Label; $ctl.Text = $t["cpuT"]; $ctl.Font = New-Object System.Drawing.Font("Arial", 7); $ctl.ForeColor = [System.Drawing.Color]::FromArgb(130, 130, 140); $ctl.Location = New-Object System.Drawing.Point(8, $hy2); $hw.Controls.Add($ctl)
$ctt = New-Object System.Windows.Forms.TextBox; $ctt.Location = New-Object System.Drawing.Point(8, $hy2+12); $ctt.Size = New-Object System.Drawing.Size(65, 24); $ctt.Text = "max"; $ctt.BackColor = [System.Drawing.Color]::FromArgb(55, 55, 65); $ctt.ForeColor = [System.Drawing.Color]::White; $hw.Controls.Add($ctt)

$msl = New-Object System.Windows.Forms.Label; $msl.Text = $t["mouse"]; $msl.Font = New-Object System.Drawing.Font("Arial", 7); $msl.ForeColor = [System.Drawing.Color]::FromArgb(130, 130, 140); $msl.Location = New-Object System.Drawing.Point(85, $hy2); $hw.Controls.Add($msl)
$msc = New-Object System.Windows.Forms.ComboBox; $msc.Location = New-Object System.Drawing.Point(85, $hy2+12); $msc.Size = New-Object System.Drawing.Size(85, 24); $msc.Items.Add("usb-tablet"); $msc.Items.Add("usb-mouse"); $msc.SelectedItem = "usb-tablet"; $msc.BackColor = [System.Drawing.Color]::FromArgb(55, 55, 65); $msc.ForeColor = [System.Drawing.Color]::White; $hw.Controls.Add($msc)

$kbl = New-Object System.Windows.Forms.Label; $kbl.Text = $t["kb"]; $kbl.Font = New-Object System.Drawing.Font("Arial", 7); $kbl.ForeColor = [System.Drawing.Color]::FromArgb(130, 130, 140); $kbl.Location = New-Object System.Drawing.Point(185, $hy2); $hw.Controls.Add($kbl)
$kbd = New-Object System.Windows.Forms.ComboBox; $kbd.Location = New-Object System.Drawing.Point(185, $hy2+12); $kbd.Size = New-Object System.Drawing.Size(75, 24); $kbd.Items.Add("usb-kbd"); $kbd.SelectedItem = "usb-kbd"; $kbd.BackColor = [System.Drawing.Color]::FromArgb(55, 55, 65); $kbd.ForeColor = [System.Drawing.Color]::White; $hw.Controls.Add($kbd)

$y += 110

# Boot label
$bl = New-Object System.Windows.Forms.Label
$bl.Text = $t["boot"]
$bl.Font = New-Object System.Windows.Forms.Font("Arial", 8, [System.Drawing.FontStyle]::Bold)
$bl.ForeColor = [System.Drawing.Color]::FromArgb(80, 160, 255)
$bl.Location = New-Object System.Drawing.Point(15, $y)
$f.Controls.Add($bl)
$y += 18

$bi = New-Object System.Windows.Forms.RadioButton
$bi.Text = $t["bootIso"]
$bi.Location = New-Object System.Drawing.Point(15, $y)
$bi.ForeColor = [System.Drawing.Color]::White
$bi.Checked = $true
$f.Controls.Add($bi)

$bd = New-Object System.Windows.Forms.RadioButton
$bd.Text = $t["bootDisk"]
$bd.Location = New-Object System.Drawing.Point(160, $y)
$bd.ForeColor = [System.Drawing.Color]::White
$f.Controls.Add($bd)
$y += 25

$dt = New-Object System.Windows.Forms.TextBox
$dt.Location = New-Object System.Drawing.Point(15, $y)
$dt.Size = New-Object System.Drawing.Size(290, 25)
$dt.Text = "C:\Haiku\macos_disk.img"
$dt.BackColor = [System.Drawing.Color]::FromArgb(45, 45, 55)
$dt.ForeColor = [System.Drawing.Color]::White
$f.Controls.Add($dt)

$db = New-Object System.Windows.Forms.Button
$db.Text = $t["browse"]
$db.Location = New-Object System.Drawing.Point(310, $y)
$db.Size = New-Object System.Drawing.Size(90, 25)
$db.BackColor = [System.Drawing.Color]::FromArgb(60, 60, 75)
$db.ForeColor = [System.Drawing.Color]::White
$db.FlatStyle = "Flat"
$db.Add_Click({ $d = New-Object System.Windows.Forms.OpenFileDialog; $d.Filter = "Disk (*.img;*.qcow2)|*.img;*.qcow2"; if ($d.ShowDialog() -eq "OK") { $dt.Text = $d.FileName } })
$f.Controls.Add($db)
$y += 32

$ndb = New-Object System.Windows.Forms.Button
$ndb.Text = $t["newDisk"]
$ndb.Location = New-Object System.Drawing.Point(15, $y)
$ndb.Size = New-Object System.Drawing.Size(160, 30)
$ndb.BackColor = [System.Drawing.Color]::FromArgb(40, 90, 65)
$ndb.ForeColor = [System.Drawing.Color]::White
$ndb.FlatStyle = "Flat"
$ndb.Add_Click({ $d = New-Object System.Windows.Forms.SaveFileDialog; $d.Filter = "Raw (*.img)|*.img|QCOW2 (*.qcow2)|*.qcow2"; $d.FileName = "new_disk.img"; if ($d.ShowDialog() -eq "OK") { $fmt = if ($d.FileName -match "qcow2") { "qcow2" } else { "raw" }; $p = Start-Process "C:\Program Files\qemu\qemu-img.exe" -ArgumentList "create", "-f", $fmt, "`"$($d.FileName)`"", "10G" -Wait -PassThru -WindowStyle Hidden; if ($p.ExitCode -eq 0) { $dt.Text = $d.FileName; [System.Windows.Forms.MessageBox]::Show("$($t['created']):`n$($d.FileName)", $t["done"]) } } })
$f.Controls.Add($ndb)
$y += 40

# Start button
$sb = New-Object System.Windows.Forms.Button
$sb.Text = $t["start"]
$sb.Location = New-Object System.Drawing.Point(15, $y)
$sb.Size = New-Object System.Drawing.Size(385, 45)
$sb.BackColor = [System.Drawing.Color]::FromArgb(0, 145, 65)
$sb.ForeColor = [System.Drawing.Color]::White
$sb.Font = New-Object System.Drawing.Font("Arial", 11, [System.Drawing.FontStyle]::Bold)
$sb.FlatStyle = "Flat"
$sb.Add_Click({
    $iso = $it.Text
    $arch = if ($ac.SelectedItem -eq "PowerPC") { "ppc" } else { "x86_64" }
    $ram = [int]$rn.Value
    $cpus = [int]$cn.Value
    $mach = $mt.Text
    $cpu = $ctt.Text
    $mouse = $msc.SelectedItem
    $kb = $kbd.SelectedItem
    if ($bi.Checked) {
        if (-not (Test-Path $iso)) { [System.Windows.Forms.MessageBox]::Show($t["isoErr"], $t["err"]); return }
    } else {
        if (-not (Test-Path $dt.Text)) { [System.Windows.Forms.MessageBox]::Show($t["diskErr"], $t["err"]); return }
    }
    if ($arch -eq "ppc") {
        $exe = "C:\Program Files\qemu\qemu-system-ppc.exe"
        if ($bi.Checked) { $args = "-cdrom `"$iso`" -m $ram -boot d -display sdl -machine $mach -cpu $cpu -smp $cpus -device $mouse -device $kb" }
        else { $fmt = if ($dt.Text -match "qcow2") { "qcow2" } else { "raw" }; $args = "-drive file=`"$($dt.Text)`",format=$fmt,media=disk -m $ram -boot c -display sdl -machine $mach -cpu $cpu -smp $cpus -device $mouse -device $kb" }
    } else {
        $exe = "C:\Program Files\qemu\qemu-system-x86_64.exe"
        if ($bi.Checked) { $args = "-cdrom `"$iso`" -m $ram -boot d -display sdl -machine $mach -smp $cpus -usb -device $mouse -device $kb" }
        else { $fmt = if ($dt.Text -match "qcow2") { "qcow2" } else { "raw" }; $args = "-drive file=`"$($dt.Text)`",format=$fmt,index=0,media=disk -m $ram -boot c -display sdl -machine $mach -smp $cpus -usb -device $mouse -device $kb" }
    }
    Start-Process $exe -ArgumentList $args -WindowStyle Minimized
    $sl.Text = "Running $arch..."
})
$f.Controls.Add($sb)
$y += 52

# Status
$sl = New-Object System.Windows.Forms.Label
$sl.Text = $t["ready"]
$sl.Font = New-Object System.Drawing.Font("Arial", 8)
$sl.ForeColor = [System.Drawing.Color]::FromArgb(0, 190, 90)
$sl.Location = New-Object System.Drawing.Point(15, $y)
$f.Controls.Add($sl)

$f.ShowDialog()
