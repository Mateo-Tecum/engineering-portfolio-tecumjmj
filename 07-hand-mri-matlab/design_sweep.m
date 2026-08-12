function results = design_sweep(radii, lengths, N, I, handLength)
%DESIGN_SWEEP Compare simple solenoid geometries by axial field uniformity.
%
% Lower peak-to-peak deviation is better, but this function intentionally
% does not declare an optimum because a real design also requires constraints
% on field target, power, wire gauge, thermal behavior, manufacturability,
% and ultimately MRI-specific system requirements.

mu0 = 4*pi*1e-7;
rows = [];

for i = 1:numel(radii)
    for j = 1:numel(lengths)
        R = radii(i);
        L = lengths(j);

        z = linspace(-handLength/2,handLength/2,401);
        B = solenoid_field_axis(z,R,L,N,I);
        B0 = solenoid_field_axis(0,R,L,N,I);

        dev = 100*(B-B0)/B0;
        ptp = max(dev)-min(dev);

        longApprox = mu0*(N/L)*I;

        rows = [rows; R L B0 longApprox ptp]; %#ok<AGROW>
    end
end

results = array2table(rows,'VariableNames', ...
    {'Radius_m','Length_m','CenterField_T','LongSolenoidApprox_T', ...
     'AxialPeakToPeakDeviation_pct'});

results = sortrows(results,'AxialPeakToPeakDeviation_pct','ascend');
end
