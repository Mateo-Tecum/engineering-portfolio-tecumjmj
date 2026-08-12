HAND MRI - MATLAB DESIGN STUDY
================================

Files
-----
run_hand_mri_project.m
    Main script. Start here.

solenoid_field_axis.m
    Closed-form axial magnetic field of a finite solenoid.

solenoid_field_map.m
    Numerical Biot-Savart field map in an axisymmetric r-z plane.

design_sweep.m
    Compares several candidate coil radii and lengths.

What MATLAB produces
--------------------
plot_01_axial_field.png
plot_02_axial_error.png
plot_03_larmor_frequency.png
plot_04_field_map.png
plot_05_field_nonuniformity.png
design_sweep_results.csv
hand_mri_results.mat

How to run
----------
1. Put all .m files in the same folder.
2. Open MATLAB.
3. Set MATLAB's Current Folder to this folder.
4. Open run_hand_mri_project.m.
5. Click Run.
6. Edit values under "USER-EDITABLE DESIGN INPUTS" and rerun.

Important
---------
This package is intended for computational/engineering study and portfolio
development. It is not a validated human-use MRI design and should not be
treated as a construction or safety specification.

Suggested next steps
--------------------
1. Validate the finite-solenoid model against simple analytical cases.
2. Refine geometry based on a realistic hand envelope.
3. CAD the selected geometry in SolidWorks.
4. Add thermal/current-density estimates.
5. Add a gradient-coil model as a separate subsystem.
6. Add RF/B1 coil modeling as a separate subsystem.
7. Use an educational Bloch-equation MRI simulation for signal formation and
   image reconstruction rather than treating the B0 solenoid alone as a full MRI.
