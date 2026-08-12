%% HAND MRI SOLENOID DESIGN STUDY
% Main entry point for the Hand MRI computational design project.
% Run this file first.
%
% This is an engineering/educational field-design model, NOT a validated
% human-use MRI scanner design.

clear; clc; close all;

%% 1. USER-EDITABLE DESIGN INPUTS
p.mu0 = 4*pi*1e-7;              % permeability of free space [T*m/A]
p.gamma_hz = 42.57747892e6;     % proton gyromagnetic ratio gamma/(2*pi) [Hz/T]

p.coilRadius = 0.070;           % solenoid mean radius [m]
p.coilLength = 0.220;           % solenoid length [m]
p.turns = 300;                  % total turns
p.current = 2.0;                % current [A]

p.wireDiameter = 0.0010;        % conductor diameter [m], simplified
p.copperResistivity = 1.68e-8;  % copper resistivity [ohm*m]

% Approximate hand region to evaluate
p.handLength = 0.180;           % hand region length [m]
p.handRadius = 0.045;           % hand region radius [m]

fprintf('==============================================\n');
fprintf(' HAND MRI - SOLENOID DESIGN STUDY\n');
fprintf('==============================================\n\n');

%% 2. FIRST-ORDER DESIGN CALCULATIONS
n = p.turns / p.coilLength;                    % turns per meter
B_long = p.mu0 * n * p.current;                % long-solenoid approximation [T]
f_long = p.gamma_hz * B_long;                  % Larmor frequency [Hz]

wireLength = 2*pi*p.coilRadius*p.turns;         % simple approximation [m]
wireArea = pi*(p.wireDiameter/2)^2;             % conductor cross-sectional area [m^2]
wireResistance = p.copperResistivity*wireLength/wireArea;
power = p.current^2 * wireResistance;
voltage = p.current * wireResistance;

fprintf('Coil radius:              %.1f mm\n', p.coilRadius*1000);
fprintf('Coil length:              %.1f mm\n', p.coilLength*1000);
fprintf('Turns:                    %d\n', p.turns);
fprintf('Current:                  %.2f A\n', p.current);
fprintf('Long-solenoid B0:         %.6f T (%.3f mT)\n', B_long, B_long*1e3);
fprintf('Estimated Larmor freq.:   %.3f MHz\n', f_long/1e6);
fprintf('Approx. wire length:      %.1f m\n', wireLength);
fprintf('Approx. resistance:       %.3f ohm\n', wireResistance);
fprintf('Approx. voltage:          %.2f V\n', voltage);
fprintf('Approx. resistive power:  %.2f W\n\n', power);

%% 3. FINITE-SOLENOID AXIAL FIELD
z = linspace(-0.20, 0.20, 801);
Bz_axis = solenoid_field_axis(z, p.coilRadius, p.coilLength, ...
    p.turns, p.current);

Bcenter = solenoid_field_axis(0, p.coilRadius, p.coilLength, ...
    p.turns, p.current);
fcenter = p.gamma_hz*Bcenter;

fprintf('Finite-solenoid center B0: %.6f T (%.3f mT)\n', Bcenter, Bcenter*1e3);
fprintf('Center Larmor frequency:    %.3f MHz\n\n', fcenter/1e6);

% Evaluate axial homogeneity across the hand length
handMask = abs(z) <= p.handLength/2;
B_hand_axis = Bz_axis(handMask);
axisDeviationPct = 100*(B_hand_axis - Bcenter)/Bcenter;
axisPeakToPeakPct = max(axisDeviationPct)-min(axisDeviationPct);

fprintf('Axial peak-to-peak variation across %.0f mm hand region: %.3f %%\n\n', ...
    p.handLength*1000, axisPeakToPeakPct);

%% 4. PLOT: AXIAL MAGNETIC FIELD
figure('Name','Axial Magnetic Field');
plot(z*1000, Bz_axis*1e3, 'LineWidth', 1.8);
grid on;
xlabel('Axial position z [mm]');
ylabel('B_z [mT]');
title('Finite-solenoid axial magnetic field');
xline(-p.handLength*500, '--', 'Hand ROI');
xline( p.handLength*500, '--');
saveas(gcf,'plot_01_axial_field.png');

%% 5. PLOT: AXIAL FIELD ERROR
figure('Name','Axial Field Error');
plot(z(handMask)*1000, axisDeviationPct, 'LineWidth', 1.8);
grid on;
xlabel('Axial position z [mm]');
ylabel('Deviation from center field [%]');
title('Axial B_0 nonuniformity across proposed hand region');
yline(0,'--');
saveas(gcf,'plot_02_axial_error.png');

%% 6. PLOT: LARMOR FREQUENCY ALONG AXIS
f_axis = p.gamma_hz * Bz_axis;
figure('Name','Larmor Frequency');
plot(z*1000, f_axis/1e6, 'LineWidth', 1.8);
grid on;
xlabel('Axial position z [mm]');
ylabel('Proton Larmor frequency [MHz]');
title('Larmor frequency implied by local B_0');
xline(-p.handLength*500, '--');
xline( p.handLength*500, '--');
saveas(gcf,'plot_03_larmor_frequency.png');

%% 7. 2-D FIELD MAP USING DISCRETIZED BIOT-SAVART LOOPS
% This is slower but much more informative than the axial closed-form model.
Nr = 81;
Nz = 121;
rVec = linspace(0, p.handRadius*1.35, Nr);
zVec = linspace(-p.handLength*0.65, p.handLength*0.65, Nz);

fprintf('Computing 2-D discretized Biot-Savart field map...\n');
[Bz_map, Br_map] = solenoid_field_map(rVec, zVec, p.coilRadius, ...
    p.coilLength, p.turns, p.current);

Bmag_map = sqrt(Bz_map.^2 + Br_map.^2);

% Approximate hand ROI
[R,Z] = meshgrid(rVec,zVec);
roi = (R <= p.handRadius) & (abs(Z) <= p.handLength/2);

B_roi = Bmag_map(roi);
B_roi_mean = mean(B_roi);
roiDeviationPct = 100*(B_roi-B_roi_mean)/B_roi_mean;

fprintf('2-D ROI mean |B|:                    %.3f mT\n', B_roi_mean*1e3);
fprintf('2-D ROI peak-to-peak nonuniformity:  %.3f %%\n', ...
    max(roiDeviationPct)-min(roiDeviationPct));
fprintf('2-D ROI RMS nonuniformity:           %.3f %%\n\n', ...
    sqrt(mean(roiDeviationPct.^2)));

%% 8. PLOT: 2-D FIELD MAGNITUDE
figure('Name','2-D Field Magnitude');
imagesc(rVec*1000, zVec*1000, Bmag_map*1e3);
axis xy;
xlabel('Radius r [mm]');
ylabel('Axial position z [mm]');
title('|B| field map - discretized Biot-Savart model');
cb = colorbar;
ylabel(cb,'|B| [mT]');
hold on;
rectangle('Position',[0 -p.handLength*500 p.handRadius*1000 p.handLength*1000], ...
    'LineStyle','--','LineWidth',1.5);
hold off;
saveas(gcf,'plot_04_field_map.png');

%% 9. PLOT: 2-D FIELD NONUNIFORMITY IN ROI
devMap = 100*(Bmag_map-B_roi_mean)/B_roi_mean;
figure('Name','2-D Field Nonuniformity');
imagesc(rVec*1000, zVec*1000, devMap);
axis xy;
xlabel('Radius r [mm]');
ylabel('Axial position z [mm]');
title('Field deviation from ROI mean');
cb = colorbar;
ylabel(cb,'Deviation [%]');
hold on;
rectangle('Position',[0 -p.handLength*500 p.handRadius*1000 p.handLength*1000], ...
    'LineStyle','--','LineWidth',1.5);
hold off;
saveas(gcf,'plot_05_field_nonuniformity.png');

%% 10. SIMPLE DESIGN SWEEP
% Compares candidate radii and lengths while maintaining the same N and I.
candidateRadii = [0.055 0.065 0.075 0.085];
candidateLengths = [0.16 0.20 0.24 0.28];

results = design_sweep(candidateRadii, candidateLengths, p.turns, ...
    p.current, p.handLength);

disp('Candidate geometry sweep:');
disp(results);

writetable(results,'design_sweep_results.csv');

%% 11. SAVE PROJECT DATA
save('hand_mri_results.mat','p','z','Bz_axis','f_axis','rVec','zVec', ...
    'Bz_map','Br_map','Bmag_map','results');

fprintf('\nFinished.\n');
fprintf('Plots 01-05, design_sweep_results.csv, and hand_mri_results.mat were saved.\n');
